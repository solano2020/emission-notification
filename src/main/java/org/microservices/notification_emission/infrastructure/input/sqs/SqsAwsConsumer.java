package org.microservices.notification_emission.infrastructure.input.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.ws.rs.GET;
import lombok.extern.slf4j.Slf4j;
import org.microservices.notification_emission.application.servcice.dto.SendEmissionNotificationRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Path("sqs/")
public class SqsAwsConsumer {

    SqsClient sqs;

    @ConfigProperty(name = "quarkus.sqs.queue-url")
    String queueUrl;

    public SqsAwsConsumer(SqsClient sqs) {
        this.sqs = sqs;
    }

    static ObjectReader NOTIFICATION_READER = new ObjectMapper().readerFor(SendEmissionNotificationRequest.class);

    @GET
    public List<SendEmissionNotificationRequest> receive() {
        List<Message> messages = sqs.receiveMessage(m -> m.maxNumberOfMessages(10).queueUrl(queueUrl)).messages();

        log.info(messages.toString());
        return messages.stream()
                .map(Message::body)
                .map(this::toNotification)
                .collect(Collectors.toList());
    }

    private SendEmissionNotificationRequest toNotification(String message) {
        SendEmissionNotificationRequest notificationRequest;
        try {
            notificationRequest = NOTIFICATION_READER.readValue(message);
        } catch (Exception e) {
            log.error("Error decoding message", e);
            throw new RuntimeException(e);
        }
        return notificationRequest;
    }

}