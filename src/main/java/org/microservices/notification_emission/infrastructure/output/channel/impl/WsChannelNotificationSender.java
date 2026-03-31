package org.microservices.notification_emission.infrastructure.output.channel.impl;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.microservices.notification_emission.domain.model.Emission;
import org.microservices.notification_emission.domain.model.EmissionNotification;
import org.microservices.notification_emission.domain.model.vo.ShippingChannel;
import org.microservices.notification_emission.domain.model.vo.StatusNotification;
import org.microservices.notification_emission.domain.ports.channel.ChannelNotificationSender;

@Slf4j
@ApplicationScoped
public class WsChannelNotificationSender implements ChannelNotificationSender {


    @Override
    public EmissionNotification send(Emission emission, ShippingChannel channel) {
        log.info("Enviando notificacion via webservice...");
        //Logica de envio
        return EmissionNotification.create(
                emission.getInsurance(),
                channel,
                StatusNotification.SUCCESSFUL,
                "OK"
        );
    }
}
