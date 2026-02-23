package org.microservices.notification_emission.infrastructure.input.sqs;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.microservices.notification_emission.application.servcice.dto.SendEmissionNotificationRequest;
import org.microservices.notification_emission.infrastructure.input.sqs.service.SqsMessageProcessingService;

@Slf4j
@Path("sqs/")
@RequestScoped
public class SqsAwsSyncConsumer {

    private final SqsMessageProcessingService sqsMessageProcessingService;

    public SqsAwsSyncConsumer(SqsMessageProcessingService sqsMessageProcessingService) {
        this.sqsMessageProcessingService = sqsMessageProcessingService;
    }


    @Tag(name = "SQS", description = "Endpoint usado sincronamente bajo demanda, en caso de usar lambda")
    @GET
    @Operation(
            summary = "Recibe mensajes desde SQS",
            description = "Lee hasta 10 mensajes desde la cola configurada y devuelve el payload parseado. "
    )
    @APIResponse(
            responseCode = "200",
            description = "Lista de mensajes parseados",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SendEmissionNotificationRequest.class)
            )
    )
    public Response receive() {
        var issuanceNotificationsSent = sqsMessageProcessingService.execute();
        return Response.ok(issuanceNotificationsSent).build();
    }

}
