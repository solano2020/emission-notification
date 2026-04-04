package org.microservices.notification_emission.infrastructure.output.channel.impl;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.microservices.notification_emission.domain.model.Emission;
import org.microservices.notification_emission.domain.model.EmissionNotification;
import org.microservices.notification_emission.domain.model.vo.ShippingChannel;
import org.microservices.notification_emission.domain.model.vo.StatusNotification;
import org.microservices.notification_emission.infrastructure.output.channel.strategy.NotificationStrategy;

@Slf4j
@ApplicationScoped
public class MailpitStrategy implements NotificationStrategy {

    @Inject
    Mailer mailer;

    @Override
    public ShippingChannel getType() {
        return ShippingChannel.EMAIL;
    }

    @Override
    public EmissionNotification send(Emission emission) {
        log.info("Enviando notificacion via Email...");
        mailer.send(
                Mail.withText("quarkus@quarkus.io",
                        "Ahoy from Quarkus",
                        "A simple email sent from a Quarkus application."
                )
        );

        return EmissionNotification.create(
                emission.getInsurance(),
                ShippingChannel.EMAIL,
                StatusNotification.SUCCESSFUL,
                "OK"
        );
    }
}
