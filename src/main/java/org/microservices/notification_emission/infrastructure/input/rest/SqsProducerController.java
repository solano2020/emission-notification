package org.microservices.notification_emission.infrastructure.input.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.microservices.notification_emission.application.servcice.dto.SendEmissionNotificationRequest;
import org.microservices.notification_emission.infrastructure.output.sqs.SqsAwsProducer;

@Path("/sqs")
@RequestScoped
public class SqsProducerController {

    private final SqsAwsProducer sqsAwsProducer;

    public SqsProducerController(SqsAwsProducer sqsAwsProducer) {
        this.sqsAwsProducer = sqsAwsProducer;
    }

    @POST
    @Path("/messages")
    public Response publish(SendEmissionNotificationRequest request) {
        sqsAwsProducer.send(request);
        return Response.accepted().build();
    }
}
