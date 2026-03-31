package org.microservices.notification_emission.domain.model;

public class Emission {

    private final Long insurance;
    private final VehicleRegistration vehicleRegistration;

    private Emission(Long insurance, VehicleRegistration vehicleRegistration) {
        validateInsurance(insurance);
        validateRegistration(vehicleRegistration);
        this.insurance = insurance;
        this.vehicleRegistration = vehicleRegistration;
    }

    public static Emission create(Long insurance, VehicleRegistration vehicleRegistration) {
        return new Emission(insurance, vehicleRegistration);
    }

    private void validateInsurance(Long insurance) {
        if (insurance == null || insurance <= 0) {
            throw new IllegalArgumentException("Debe contiener un identificador para el seguro");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Debe contiener un identificador para la emission");
        }
    }

    private void validateRegistration(VehicleRegistration vehicleRegistration) {
        if (vehicleRegistration == null) {
            throw new IllegalArgumentException("Debe contener matricula");
        }
    }

    public Long getInsurance() {
        return insurance;
    }

    public VehicleRegistration getVehicleRegistration() {
        return vehicleRegistration;
    }
}
