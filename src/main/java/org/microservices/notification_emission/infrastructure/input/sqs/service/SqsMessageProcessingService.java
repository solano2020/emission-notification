package org.microservices.notification_emission.infrastructure.input.sqs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.microservices.notification_emission.application.servcice.SendEmissionNotificationUseCase;
import org.microservices.notification_emission.application.servcice.dto.SendEmissionNotificationRequest;
import org.microservices.notification_emission.domain.exception.EmissionNotFoundException;
import org.microservices.notification_emission.infrastructure.input.sqs.exception.NonRetryableMessageException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class SqsMessageProcessingService {

    @ConfigProperty(name = "quarkus.sqs.queue-url")
    String queueUrl;

    private final SqsClient sqs;

    private final ObjectReader notificationReader;
    private final SendEmissionNotificationUseCase sendEmissionNotificationUseCase;

    public SqsMessageProcessingService(SqsClient sqs, ObjectMapper objectMapper, SendEmissionNotificationUseCase sendEmissionNotificationUseCase) {
        this.sqs = sqs;
        this.notificationReader = objectMapper.readerFor(SendEmissionNotificationRequest.class);
        this.sendEmissionNotificationUseCase = sendEmissionNotificationUseCase;
    }

    /**
     * Obtiene los mensajes registrados en la cola para luego ser notificados
     * @return una lista de SendEmissionNotificationRequest que fueron procesados exitosamente
     */
    public List<SendEmissionNotificationRequest> execute(){
        //Obtenemos la lista de mensajes de la cola
        List<Message> messages = sqs.receiveMessage(m -> m
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(20) // para evitar traer mensajes vacios
        ).messages();

        log.info("Total de mensajes recibidos: {}", messages.size());

        //si no hay mensajes por procesar devolvemos una lista vacia
        if (messages.isEmpty()) {
            return Collections.emptyList();
        }

        //creamos una lista de emisiones que seran procesadas y una lista de mensajes de cola a eliminar
        List<SendEmissionNotificationRequest> processed = new ArrayList<>();
        List<DeleteMessageBatchRequestEntry> deleteEntries = new ArrayList<>();

        //Recorremos las emisiones de poliza obtenidas de la cola
        for (Message msg : messages) {
            try {
                SendEmissionNotificationRequest request =  toNotification(msg.body());
                //Validamos datos requeridos
                validateRequest(request);

                //procesamos la emision notificandola
                var responseNotification = sendEmissionNotificationUseCase.execute(request);

                // si la emision se notifico correctamente la agregamos a la lista de procesadas y a la lista de mensajes a eliminar en la cola
                if(responseNotification.isSentNotification()){
                    processed.add(request);
                    deleteEntries.add(
                            DeleteMessageBatchRequestEntry.builder()
                                    .id(UUID.randomUUID().toString())
                                    .receiptHandle(msg.receiptHandle())
                                    .build()
                    );
                }

            } catch (Exception e) {
                //Si existe un fallo relacionado a datos requesridos, lo agregamos a la lista de mensajes a eliminar
                if (isNonRetryable(e)) {
                    deleteEntries.add(
                            DeleteMessageBatchRequestEntry.builder()
                                    .id(UUID.randomUUID().toString())
                                    .receiptHandle(msg.receiptHandle())
                                    .build()
                    );
                    log.error("Mensaje no reintentable. Se eliminará de la cola. detail: [{}]", e.getMessage(), e);
                    continue;
                }

                log.error("Error procesando mensaje de la cola. Se reintentará según visibility timeout.", e);
                // ❌ No lo agregamos al batch delete
                // SQS lo reintentará según visibility timeout
            }
        }

        // Eliminación en batch (solo los exitosos)
        if (!deleteEntries.isEmpty()) {
            //Enviamos a eliminar los mensajes que se an enviado exitosamente de la cola
            var deleteResponse = sqs.deleteMessageBatch(DeleteMessageBatchRequest.builder()
                    .queueUrl(queueUrl)
                    .entries(deleteEntries)
                    .build());

            //Obtenemos los mensajes que se eliminaron de la cola, exitosos y fallidos
            List<DeleteMessageBatchResultEntry> successful = deleteResponse.successful();
            List<BatchResultErrorEntry> failed = deleteResponse.failed();

            log.info("Mensajes eliminados correctamente: {}", successful.size());
            //Si existe mensajes que no se eliminaron correctamente, los mostramos en el log
            if (!failed.isEmpty()) {
                for (BatchResultErrorEntry fail : failed) {
                    log.error(
                            "Fallo al eliminar mensaje de SQS. id: [{}], code: [{}], message: [{}], senderFault: [{}]",
                            fail.id(),
                            fail.code(),
                            fail.message(),
                            fail.senderFault()
                    );
                }
            }
        }
        return processed;
    }

    /**
     * Convierte el body del mensaje SQS a tipo SendEmissionNotification
     * @param message body del mensdaje desde la cola
     * @return un objeto de tipo SendEmissionNotification
     */
    private SendEmissionNotificationRequest toNotification(String message) {
        SendEmissionNotificationRequest notificationRequest;
        try {
            notificationRequest = notificationReader.readValue(message);
        } catch (Exception e) {
            log.error("Error decoding message", e);
            throw new NonRetryableMessageException("Mensaje SQS con payload invalido", e);
        }
        return notificationRequest;
    }


    /**
     * Metodo que valida la informacion del DTO SendEmissionNotificationRequest para su procesamiento
     * @param request de tipo SendEmissionNotificationRequest
     */
    private void validateRequest(SendEmissionNotificationRequest request) {
        if (request == null || request.getEmissionId() == null || request.getShippingChannel() == null) {
            throw new NonRetryableMessageException("Mensaje SQS sin datos requeridos");
        }
    }

    /**
     * Valida el tipo la excepcion dentro de las consideradas para el no reintento de envio de notificacion
     * @param e de tipo Exception general
     * @return true si no se debe reintentar, de lo contrario false
     */
    private boolean isNonRetryable(Exception e) {
        return e instanceof NonRetryableMessageException || e instanceof EmissionNotFoundException;
    }


}
