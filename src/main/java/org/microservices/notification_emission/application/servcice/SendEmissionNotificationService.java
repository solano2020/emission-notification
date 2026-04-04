package org.microservices.notification_emission.application.servcice;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.microservices.notification_emission.application.servcice.dto.SendEmissionNotificationRequest;
import org.microservices.notification_emission.application.servcice.dto.SendEmissionNotificationResponse;
import org.microservices.notification_emission.domain.exception.EmissionNotFoundException;
import org.microservices.notification_emission.domain.model.vo.ShippingChannel;
import org.microservices.notification_emission.domain.model.vo.StatusNotification;
import org.microservices.notification_emission.domain.ports.repository.EmissionRepository;
import org.microservices.notification_emission.domain.ports.repository.NotificationEmissionRepository;
import org.microservices.notification_emission.infrastructure.output.channel.strategy.NotificationService;


@Slf4j
@ApplicationScoped
public class SendEmissionNotificationService implements SendEmissionNotificationUseCase{

    private final EmissionRepository emissionRepository;
    private final NotificationEmissionRepository notificationEmissionRepository;
    private final NotificationService notificationService;

    public SendEmissionNotificationService(EmissionRepository emissionRepository, NotificationEmissionRepository notificationEmissionRepository, NotificationService notificationService) {
        this.emissionRepository = emissionRepository;
        this.notificationEmissionRepository = notificationEmissionRepository;
        this.notificationService = notificationService;
    }

    @Override
    public SendEmissionNotificationResponse execute(SendEmissionNotificationRequest request) {
        var emission = emissionRepository.find(request.getInsuranceId()).orElseThrow(() -> new EmissionNotFoundException("Emission no encontrada id: "+ request.getInsuranceId()));
        var emissionNotification = notificationService.send(ShippingChannel.fromValue(request.getShippingChannel().name()), emission);
        notificationEmissionRepository.save(emissionNotification);
        return new SendEmissionNotificationResponse(
                StatusNotification.SUCCESSFUL.equals(emissionNotification.getStatus())
        );
    }
}
