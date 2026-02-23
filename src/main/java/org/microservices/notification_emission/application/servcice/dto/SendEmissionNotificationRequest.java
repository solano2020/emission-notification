package org.microservices.notification_emission.application.servcice.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.microservices.notification_emission.application.servcice.dto.data.ShippingChannel;

@Getter
public class SendEmissionNotificationRequest {

    @NotNull(message = "El identificador de la emision es obligatorio")
    Long emissionId;

    @NotNull(message = "El tipo de canal de envio es obligatorio. ejemplo: WEBSERVICE")
    ShippingChannel shippingChannel;

}
