package it.gov.pagopa.mocker.service;

import it.gov.pagopa.mocker.model.AppInfo;
import it.gov.pagopa.mocker.repository.HealthCheckRepository;
import it.gov.pagopa.mocker.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@Slf4j
public class HealthCheckService {

    @Autowired
    private Environment env;

    @Autowired
    private HealthCheckRepository dao;

    @Autowired
    private CacheService cacheService;

    public AppInfo getAppInfo()  {
        return AppInfo.builder()
                .name(env.getProperty("application.name", String.class))
                .version(env.getProperty("application.version", String.class))
                .environment(env.getProperty("application.environment", String.class))
                .dbConnection(checkDBConnection() ? "up" : "down")
                .redisConnection(checkRedisConnection() ? "up" : "down")
                .build();
    }

    private boolean checkDBConnection() {
        try {
            return dao.health().isPresent();
        } catch (DataAccessException e) {
            return false;
        }
    }

    private boolean checkRedisConnection() {
        try {
            return cacheService.healthCheck();
        } catch (Exception e) {
            return false;
        }
    }
}
