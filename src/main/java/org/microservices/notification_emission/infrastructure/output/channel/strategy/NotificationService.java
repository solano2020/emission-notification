package org.microservices.notification_emission.infrastructure.output.channel.strategy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import org.microservices.notification_emission.domain.model.Emission;
import org.microservices.notification_emission.domain.model.EmissionNotification;
import org.microservices.notification_emission.domain.model.vo.ShippingChannel;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class NotificationService {

    private final Map<ShippingChannel, NotificationStrategy> strategies;

    public NotificationService(Instance<NotificationStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        NotificationStrategy::getType,
                        Function.identity()
                ));
    }

    public EmissionNotification send(ShippingChannel type, Emission emission){
        return strategies.get(type).send(emission);
    }
}
