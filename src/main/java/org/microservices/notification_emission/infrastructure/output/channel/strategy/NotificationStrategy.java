package org.microservices.notification_emission.infrastructure.output.channel.strategy;

import org.microservices.notification_emission.domain.model.Emission;
import org.microservices.notification_emission.domain.model.EmissionNotification;
import org.microservices.notification_emission.domain.model.vo.ShippingChannel;

public interface NotificationStrategy {
    ShippingChannel getType();

    EmissionNotification send(Emission emission);
}
