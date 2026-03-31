package org.microservices.notification_emission.infrastructure.output.channel;

import jakarta.enterprise.context.ApplicationScoped;
import org.microservices.notification_emission.application.qualifier.ChannelAdapter;
import org.microservices.notification_emission.domain.model.Emission;
import org.microservices.notification_emission.domain.model.EmissionNotification;
import org.microservices.notification_emission.domain.model.vo.ShippingChannel;
import org.microservices.notification_emission.domain.ports.channel.ChannelNotificationSender;
import org.microservices.notification_emission.infrastructure.output.channel.factory.ChannelFactoryProvider;

@ChannelAdapter
@ApplicationScoped
public class ChannelNotificationAdapter implements ChannelNotificationSender {

    private final ChannelFactoryProvider channelFactoryProvider;

    public ChannelNotificationAdapter(ChannelFactoryProvider channelFactoryProvider) {
        this.channelFactoryProvider = channelFactoryProvider;
    }

    @Override
    public EmissionNotification send(Emission emission, ShippingChannel channel) {
        var factory =  channelFactoryProvider.getFactory(channel);
        var sender = factory.createSender();
        return sender.send(emission, channel);
    }
}
