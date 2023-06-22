package it.gov.pagopa.mocker.service;

import it.gov.pagopa.mocker.repository.HealthCheckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HealthCheckService {

    @Autowired
    private HealthCheckRepository dao;

    public boolean checkDBConnection() {
        try {
            return dao.health().isPresent();
        } catch (DataAccessResourceFailureException e) {
            return false;
        }
    }
}
