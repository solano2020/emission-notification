package org.microservices.notification_emission.infrastructure.output.channel.impl;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.microservices.notification_emission.domain.model.Emission;
import org.microservices.notification_emission.domain.model.EmissionNotification;
import org.microservices.notification_emission.domain.model.vo.ShippingChannel;
import org.microservices.notification_emission.domain.model.vo.StatusNotification;
import org.microservices.notification_emission.infrastructure.output.channel.strategy.NotificationStrategy;

@Slf4j
@ApplicationScoped
public class WsStrategy implements NotificationStrategy {


    @Override
    public ShippingChannel getType() {
        return ShippingChannel.WEBSERVICE;
    }

    @Override
    public EmissionNotification send(Emission emission) {
        log.info("Enviando notificacion via webservice...");
        //Logica de envio
        return EmissionNotification.create(
                emission.getInsurance(),
                ShippingChannel.WEBSERVICE,
                StatusNotification.SUCCESSFUL,
                "OK"
        );
    }

}
