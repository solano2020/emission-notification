package org.microservices.notification_emission.infrastructure.output.channel.impl.email;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.microservices.notification_emission.domain.model.Emission;
import org.microservices.notification_emission.domain.model.EmissionNotification;
import org.microservices.notification_emission.domain.model.vo.ShippingChannel;
import org.microservices.notification_emission.domain.ports.channel.ChannelNotificationSender;

@Slf4j
@ApplicationScoped
public class MailpitChannelNotificationSender implements ChannelNotificationSender {

    Mailer mailer;

    public MailpitChannelNotificationSender(Mailer mailer) {
        this.mailer = mailer;
    }

    @Override
    public EmissionNotification send(Emission emission, ShippingChannel channel)
    {

        mailer.send(
                Mail.withText("quarkus@quarkus.io",
                        "Ahoy from Quarkus",
                        "A simple email sent from a Quarkus application."
                )
        );
        log.info("Enviando notificacion via Email...");
        return null;
    }
}
