package org.microservices.notification_emission.infrastructure.input.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.scheduler.Scheduled;
import org.microservices.notification_emission.application.servcice.SendEmissionNotificationUseCase;
import org.microservices.notification_emission.application.servcice.dto.SendEmissionNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Slf4j
@ApplicationScoped
public class SqsAwsConsumer {

    private final SqsClient sqs;
    private final ObjectMapper objectMapper;
    private final SendEmissionNotificationUseCase sendEmissionNotificationUseCase;

    @ConfigProperty(name = "app.sqs.queue-url")
    String queueUrl;

    @ConfigProperty(name = "app.sqs.consumer.max-messages", defaultValue = "10")
    Integer maxMessages;

    public SqsAwsConsumer(SqsClient sqs, ObjectMapper objectMapper, SendEmissionNotificationUseCase sendEmissionNotificationUseCase) {
        this.sqs = sqs;
        this.objectMapper = objectMapper;
        this.sendEmissionNotificationUseCase = sendEmissionNotificationUseCase;
    }

    @Scheduled(every = "{app.sqs.consumer.poll-interval}", delayed = "{app.sqs.consumer.initial-delay}")
    void consume() {
        List<Message> messages = sqs.receiveMessage(m -> m
                .maxNumberOfMessages(maxMessages)
                .queueUrl(queueUrl)
        ).messages();

        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (Message message : messages) {
            try {
                SendEmissionNotificationRequest request = toNotification(message.body());
                sendEmissionNotificationUseCase.execute(request);
                sqs.deleteMessage(m -> m.queueUrl(queueUrl).receiptHandle(message.receiptHandle()));
            } catch (Exception e) {
                log.error("Error procesando mensaje {}", message.messageId(), e);
            }
        }
    }

    private SendEmissionNotificationRequest toNotification(String message) {
        try {
            return objectMapper.readValue(message, SendEmissionNotificationRequest.class);
        } catch (Exception e) {
            log.error("Error decoding message", e);
            throw new RuntimeException(e);
        }
    }

}
