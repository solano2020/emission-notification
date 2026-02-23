package org.microservices.notification_emission.application.servcice;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.microservices.notification_emission.application.qualifier.ChannelAdapter;
import org.microservices.notification_emission.application.servcice.dto.SendEmissionNotificationRequest;
import org.microservices.notification_emission.application.servcice.dto.SendEmissionNotificationResponse;
import org.microservices.notification_emission.domain.exception.EmissionNotFoundException;
import org.microservices.notification_emission.domain.model.vo.ShippingChannel;
import org.microservices.notification_emission.domain.model.vo.StatusNotification;
import org.microservices.notification_emission.domain.ports.channel.ChannelNotificationSender;
import org.microservices.notification_emission.domain.ports.repository.EmissionRepository;
import org.microservices.notification_emission.domain.ports.repository.NotificationEmissionRepository;



@Slf4j
@ApplicationScoped
public class SendEmissionNotificationService implements SendEmissionNotificationUseCase{

    private final EmissionRepository emissionRepository;
    private final NotificationEmissionRepository notificationEmissionRepository;
    private final ChannelNotificationSender channelNotificationSender;

    public SendEmissionNotificationService(EmissionRepository emissionRepository, NotificationEmissionRepository notificationEmissionRepository,
                                           @ChannelAdapter ChannelNotificationSender channelNotificationSender) {
        this.emissionRepository = emissionRepository;
        this.notificationEmissionRepository = notificationEmissionRepository;
        this.channelNotificationSender = channelNotificationSender;
    }

    @Override
    public SendEmissionNotificationResponse execute(SendEmissionNotificationRequest request) {
        //Varificamos que exista la emision de la poliza
        var emission = emissionRepository.find(request.getEmissionId().toString()).orElseThrow(() -> new EmissionNotFoundException("Emission no encontrada id: "+request.getEmissionId()));
        //Enviamos la notificacion de la emision de la poliza
        var emissionNotification = channelNotificationSender.send(emission, ShippingChannel.fromValue(request.getShippingChannel().name()));
        //Mandamos a guardar el resultado de la nomtificacion
        notificationEmissionRepository.save(emissionNotification);

        return new SendEmissionNotificationResponse(
                StatusNotification.SUCCESSFUL.equals(emissionNotification.getStatus())
        );
    }
}
