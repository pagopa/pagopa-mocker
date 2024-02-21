package it.gov.pagopa.mocker.service;

import it.gov.pagopa.mocker.MockerApplication;
import it.gov.pagopa.mocker.model.AppInfo;
import it.gov.pagopa.mocker.repository.HealthCheckRepository;
import it.gov.pagopa.mocker.utility.TestUtil;
import lombok.SneakyThrows;
import org.hibernate.query.internal.NativeQueryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessResourceFailureException;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = MockerApplication.class)
class HealthCheckServiceTest {

    @Autowired
    @InjectMocks
    private HealthCheckService service;

    @MockBean
    private CacheService cacheService;

    @MockBean
    private HealthCheckRepository repository;

    @Mock
    private Environment environment;

    @BeforeEach
    @SneakyThrows
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SneakyThrows
    void testOK() {

        // Mocking objects
        when(repository.health()).thenReturn(Optional.of(true));
        when(cacheService.healthCheck()).thenReturn(true);

        doReturn("mocker").when(environment).getProperty("application.name", String.class);
        doReturn("x.y.z").when(environment).getProperty("application.version", String.class);
        doReturn("test").when(environment).getProperty("application.environment", String.class);

        // Executing logic
        AppInfo result = service.getAppInfo();

        // Analyzing assertions
        String expected = TestUtil.readJsonFromFile("response/service_info_ok.json");
        JSONAssert.assertEquals(expected, result.toString(), JSONCompareMode.STRICT);
    }

    @Test
    @SneakyThrows
    void testKO() {

        // Mocking objects
        when(repository.health()).thenReturn(Optional.empty());

        doReturn("mocker").when(environment).getProperty("application.name", String.class);
        doReturn("x.y.z").when(environment).getProperty("application.version", String.class);
        doReturn("test").when(environment).getProperty("application.environment", String.class);

        // Executing logic
        AppInfo result = service.getAppInfo();

        // Analyzing assertions
        String expected = TestUtil.readJsonFromFile("response/service_info_ko.json");
        JSONAssert.assertEquals(expected, result.toString(), JSONCompareMode.STRICT);
    }

    @Test
    @SneakyThrows
    void testKO_2() {

        // Mocking objects
        DataAccessResourceFailureException exception = new DataAccessResourceFailureException("no db connection");
        doThrow(exception).when(repository).health();

        doReturn("mocker").when(environment).getProperty("application.name", String.class);
        doReturn("x.y.z").when(environment).getProperty("application.version", String.class);
        doReturn("test").when(environment).getProperty("application.environment", String.class);

        // Executing logic
        AppInfo result = service.getAppInfo();

        // Analyzing assertions
        String expected = TestUtil.readJsonFromFile("response/service_info_ko.json");
        JSONAssert.assertEquals(expected, result.toString(), JSONCompareMode.STRICT);
    }

}
