package org.microservices.notification_emission.infrastructure.output.channel.strategy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import org.microservices.notification_emission.domain.model.Emission;
import org.microservices.notification_emission.domain.model.EmissionNotification;
import org.microservices.notification_emission.domain.model.vo.ShippingChannel;
import org.microservices.notification_emission.domain.ports.channel.ChannelNotificationSender;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class NotificationService implements ChannelNotificationSender {

    private final Map<ShippingChannel, NotificationStrategy> strategies;

    public NotificationService(Instance<NotificationStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        NotificationStrategy::getType,
                        Function.identity()
                ));
    }

    @Override
    public EmissionNotification send(Emission emission, ShippingChannel channel) {
        return strategies.get(channel).send(emission);
    }
}
