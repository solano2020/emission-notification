package org.microservices.notification_emission.application.servcice.dto.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class VehicleRegistrationDto {

    @NotBlank(message = "La placa del vehiculo es obligatoria")
    String plaque;

    @JsonProperty("isIssued")
    boolean issued;

    @Pattern(regexp = "^[0-9]+$", message = "El campo debe contener solo números")
    String police;
}
