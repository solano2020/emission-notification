package org.microservices.notification_emission.infrastructure.output.channel.factory;

import jakarta.enterprise.context.ApplicationScoped;
import org.microservices.notification_emission.domain.model.vo.ShippingChannel;

@ApplicationScoped
public class ChannelFactoryProvider {

    private final WsFactory wsFactory;
    private final MailpitFactory mailpitFactory;
    private final SmsFactory smsFactory;

    public ChannelFactoryProvider(WsFactory wsFactory, MailpitFactory mailpitFactory, SmsFactory smsFactory) {
        this.wsFactory = wsFactory;
        this.mailpitFactory = mailpitFactory;
        this.smsFactory = smsFactory;
    }

    public ChannelNotificationSenderAbstractFactory getFactory(ShippingChannel channel){
        return switch (channel){
            case WEBSERVICE -> wsFactory;
            case EMAIL -> mailpitFactory;
            case SMS -> smsFactory;
        };
    }
}
