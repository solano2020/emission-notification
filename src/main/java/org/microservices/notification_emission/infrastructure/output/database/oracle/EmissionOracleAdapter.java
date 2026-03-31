package org.microservices.notification_emission.infrastructure.output.database.oracle;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.JDBCConnectionException;
import org.microservices.notification_emission.application.exception.TransientApplicationException;
import org.microservices.notification_emission.domain.model.Emission;
import org.microservices.notification_emission.domain.model.VehicleRegistration;
import org.microservices.notification_emission.domain.ports.repository.EmissionRepository;
import org.microservices.notification_emission.infrastructure.output.database.oracle.repository.InsuranceOracleRepository;

import java.sql.SQLRecoverableException;
import java.util.Optional;

@ApplicationScoped
public class EmissionOracleAdapter implements EmissionRepository {

    private final InsuranceOracleRepository insuranceOracleRepository;

    public EmissionOracleAdapter(InsuranceOracleRepository insuranceOracleRepository) {
        this.insuranceOracleRepository = insuranceOracleRepository;
    }

    @Override
    public Optional<Emission> find(Long id) {
        try {
            var insuranceEntity = insuranceOracleRepository.findByIdOptional(id);
            return insuranceEntity.map(i -> Emission.create(
                    i.getId(),
                    VehicleRegistration.create(i.getPlaque(), i.getPolicy())
            ));
        } catch (PersistenceException ex) {
            if (isTransient(ex)) {
                throw new TransientApplicationException("Fallo transitorio consultando Oracle", ex);
            }
            throw ex;
        }
    }

    private boolean isTransient(Throwable ex) {
        return ex instanceof JDBCConnectionException
                || ex instanceof SQLRecoverableException
                || (ex.getCause() != null && isTransient(ex.getCause()));
    }

}
