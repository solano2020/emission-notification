package org.microservices.notification_emission.domain.ports.repository;

import org.microservices.notification_emission.domain.model.Emission;

import java.util.Optional;

public interface EmissionRepository {
    Optional<Emission> find(Long id);
}
