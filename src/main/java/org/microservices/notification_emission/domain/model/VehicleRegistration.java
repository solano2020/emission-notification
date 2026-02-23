package org.microservices.notification_emission.domain.model;

import org.microservices.notification_emission.domain.model.vo.Plaque;

public class VehicleRegistration {

    private final Plaque plaque;
    private final String police;

    private VehicleRegistration(String plaque, String police) {
        validatePolice(police);
        this.plaque = new Plaque(plaque);
        this.police = police;
    }

    public static VehicleRegistration create(String plaque, String police) {
        return new VehicleRegistration(plaque, police);
    }

    /**
     * Valida el identificador de la poliza
     * la poliza no debe ser null o vacio, no debe contener letras y deber ser mayor o igual a 5 cifras
     *
     * @param police
     */
    public void validatePolice(String police) {
        if (police == null || police.isBlank()) {
            throw new IllegalArgumentException("El campo póliza obligatoria");
        }

        String policeNormalized = police.trim();

        if (!policeNormalized.matches("\\d+")) {
            throw new IllegalArgumentException("La póliza debe contener solo números");
        }

        if (policeNormalized.length() < 5) {
            throw new IllegalArgumentException("La póliza debe tener minimo 5 cifras");
        }


    }

    public Plaque getPlaque() {
        return plaque;
    }

    public String getPolice() {
        return police;
    }
}
