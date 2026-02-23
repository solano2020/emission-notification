package org.microservices.notification_emission.application.servcice;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.microservices.notification_emission.application.servcice.dto.SendEmissionNotificationRequest;
import org.microservices.notification_emission.application.servcice.dto.data.ShippingChannel;
import org.microservices.notification_emission.domain.model.Emission;
import org.microservices.notification_emission.domain.model.EmissionNotification;
import org.microservices.notification_emission.domain.model.VehicleRegistration;
import org.microservices.notification_emission.domain.ports.repository.EmissionRepository;
import org.microservices.notification_emission.domain.ports.repository.NotificationEmissionRepository;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class SendEmissionNotificationRequestServiceTest {

    @InjectMock
    EmissionRepository emissionRepository;

    @InjectMock
    NotificationEmissionRepository notificationEmissionRepository;

    @Inject
    SendEmissionNotificationService service;

    private SendEmissionNotificationRequest request;

    @BeforeEach
    void setUp() throws Exception {
        request = new SendEmissionNotificationRequest();
        setField(request, "emissionId", 1L);
        setField(request, "shippingChannel", ShippingChannel.WEBSERVICE);
    }

    @Test
    void execute_when_sendingNotification_IsSuccess() {
        //Creamos los datos esperados
        VehicleRegistration registration = VehicleRegistration.create("ABC123", "12345");
        Emission emission = Emission.create(1L, 10L, registration);

        //Simulamos la busqueda de la emision en la base
        when(emissionRepository.find("1")).thenReturn(Optional.of(emission));

        //Ejecutamos el metodo objetivo
        service.execute(request);

        verify(emissionRepository).find("1");
        verify(notificationEmissionRepository).save((EmissionNotification) null);
    }

    //setea los valores de los parametros usando reflexion
    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

}
