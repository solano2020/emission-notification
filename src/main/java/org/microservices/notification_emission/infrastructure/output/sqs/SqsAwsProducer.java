package org.microservices.notification_emission.infrastructure.output.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.microservices.notification_emission.application.servcice.dto.SendEmissionNotificationRequest;
import software.amazon.awssdk.services.sqs.SqsClient;

@Slf4j
@ApplicationScoped
public class SqsAwsProducer {

    private final SqsClient sqs;
    private final ObjectMapper objectMapper;

    @ConfigProperty(name = "app.sqs.queue-url")
    String queueUrl;

    public SqsAwsProducer(SqsClient sqs, ObjectMapper objectMapper) {
        this.sqs = sqs;
        this.objectMapper = objectMapper;
    }

    public void send(SendEmissionNotificationRequest request) {
        try {
            String payload = objectMapper.writeValueAsString(request);
            sqs.sendMessage(m -> m.queueUrl(queueUrl).messageBody(payload));
            log.info("Mensaje enviado a SQS para emision {}", request.getEmissionId());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("No fue posible serializar el mensaje para SQS", e);
        }
    }
}
