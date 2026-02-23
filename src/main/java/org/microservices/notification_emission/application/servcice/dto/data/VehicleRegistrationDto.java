package org.microservices.notification_emission.application.servcice.dto.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class VehicleRegistrationDto {

    @NotBlank(message = "La placa del vehiculo es obligatoria")
    String plaque;

    @Pattern(regexp = "^[0-9]+$", message = "El campo debe contener solo números")
    String police;
}
