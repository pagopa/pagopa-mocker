package it.gov.pagopa.mocker.entity.dao;


import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class HealthCheckRepository {

    @Autowired
    EntityManager entityManager;

    @Value("${healthcheck.query:select 1}")
    private String query;

    public Optional<Object> health() {
        return Optional.of(entityManager.createNativeQuery(query).getSingleResult());
    }
}
