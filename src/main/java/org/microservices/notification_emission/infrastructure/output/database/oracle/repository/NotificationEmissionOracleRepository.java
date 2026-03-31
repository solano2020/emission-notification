package org.microservices.notification_emission.infrastructure.output.database.oracle.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.microservices.notification_emission.infrastructure.output.database.oracle.Entity.NotificationEmission;

@ApplicationScoped
public class NotificationEmissionOracleRepository implements PanacheRepositoryBase<NotificationEmission, Long> {
}
