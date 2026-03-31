package org.microservices.notification_emission.infrastructure.output.database.oracle;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.JDBCConnectionException;
import org.microservices.notification_emission.application.exception.TransientApplicationException;
import org.microservices.notification_emission.domain.exception.EmissionNotFoundException;
import org.microservices.notification_emission.domain.model.EmissionNotification;
import org.microservices.notification_emission.domain.ports.repository.NotificationEmissionRepository;
import org.microservices.notification_emission.infrastructure.output.database.oracle.Entity.Insurance;
import org.microservices.notification_emission.infrastructure.output.database.oracle.Entity.NotificationEmission;
import org.microservices.notification_emission.infrastructure.output.database.oracle.repository.InsuranceOracleRepository;
import org.microservices.notification_emission.infrastructure.output.database.oracle.repository.NotificationEmissionOracleRepository;

import java.sql.SQLRecoverableException;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class NotificationEmissionOracleAdapter implements NotificationEmissionRepository {

    private final NotificationEmissionOracleRepository notificationEmissionOracleRepository;
    private final InsuranceOracleRepository insuranceOracleRepository;

    public NotificationEmissionOracleAdapter(NotificationEmissionOracleRepository notificationEmissionOracleRepository, InsuranceOracleRepository insuranceOracleRepository) {
        this.notificationEmissionOracleRepository = notificationEmissionOracleRepository;
        this.insuranceOracleRepository = insuranceOracleRepository;
    }

    @Override
    @Transactional
    public void save(EmissionNotification emissionNotification) {
        try {
            Insurance insurance = insuranceOracleRepository.findByIdOptional(emissionNotification.getInsuranceId())
                    .orElseThrow(() -> new EmissionNotFoundException(
                            "Seguro no encontrado id: " + emissionNotification.getInsuranceId()
                    ));

            NotificationEmission notificationEmission = new NotificationEmission(
                    emissionNotification.getChannel().getValue(),
                    emissionNotification.getStatus().getValue(),
                    emissionNotification.getMessage()
            );
            notificationEmission.setInsurance(insurance);

            notificationEmissionOracleRepository.persist(notificationEmission);
        } catch (PersistenceException ex) {
            if (isTransient(ex)) {
                throw new TransientApplicationException("Fallo transitorio guardando NotificationEmission en Oracle", ex);
            }
            throw ex;
        }
    }

    @Override
    public Optional<EmissionNotification> find(Long id) {
        return Optional.empty();
    }

    private boolean isTransient(Throwable ex) {
        return ex instanceof JDBCConnectionException
                || ex instanceof SQLRecoverableException
                || (ex.getCause() != null && isTransient(ex.getCause()));
    }
}
