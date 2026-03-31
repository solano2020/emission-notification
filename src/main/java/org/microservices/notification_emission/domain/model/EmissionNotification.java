package org.microservices.notification_emission.domain.model;

import org.microservices.notification_emission.domain.model.vo.ShippingChannel;
import org.microservices.notification_emission.domain.model.vo.StatusNotification;

public class EmissionNotification {

    private final Long insuranceId;
    private final ShippingChannel channel;
    private final StatusNotification status;
    private final String message;

    private EmissionNotification(Long insuranceId, ShippingChannel channel, StatusNotification status, String message) {
        validateInsuranceId(insuranceId);
        validateShippingChannel(channel);
        validateStatus(status);
        this.insuranceId = insuranceId;
        this.channel = channel;
        this.status = status;
        this.message = message;
    }

    public static EmissionNotification create(Long insuranceId, ShippingChannel channel, StatusNotification status, String message) {
        return new EmissionNotification(insuranceId, channel, status, message);
    }

    private void validateShippingChannel(ShippingChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("El canal de la notificacion emision es obligatorio");
        }
    }

    private void validateInsuranceId(Long insuranceId) {
        if (insuranceId == null) {
            throw new IllegalArgumentException("El id del seguro es obligatorio para la notificacion de emission");
        }
    }

    private void validateStatus(StatusNotification status) {
        if (status == null) {
            throw new IllegalArgumentException("El estado de la notificacion es obligatorio");
        }
    }

    public Long getInsuranceId() {
        return insuranceId;
    }

    public ShippingChannel getChannel() {
        return channel;
    }

    public StatusNotification getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
