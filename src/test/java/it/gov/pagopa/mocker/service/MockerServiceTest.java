package it.gov.pagopa.mocker.service;

import com.google.gson.Gson;
import it.gov.pagopa.mocker.MockerApplication;
import it.gov.pagopa.mocker.entity.MockResourceEntity;
import it.gov.pagopa.mocker.model.ExtractedRequest;
import it.gov.pagopa.mocker.model.ExtractedResponse;
import it.gov.pagopa.mocker.repository.MockResourceRepository;
import it.gov.pagopa.mocker.service.validator.ResourceExtractor;
import it.gov.pagopa.mocker.utility.TestUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MockerApplication.class)
class MockerServiceTest {

    @InjectMocks
    private MockerService service;

    @Spy
    private ResourceExtractor extractor;

    @MockBean
    private MockResourceRepository repository;

    @BeforeEach
    @SneakyThrows
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SneakyThrows
    void testOK_mainRule() {

        // Generating variables
        String body = "{\"name\":\"fake-ec\"}";
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(body, "POST", "ec-service/api/v1/organizations/77777777777", "application/json");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, body, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = "{\"organizationName\": \"fake-ec\",\"organizationOnboardingDate\": \"2023-06-20T15:03:56.862641\"}";

        // Mocking responses
        MockResourceEntity mockResource = new Gson().fromJson(TestUtil.readJsonFromFile("request/mock_resources/mock_resource_ok.json"), MockResourceEntity.class);
        when(repository.findById(anyString())).thenReturn(Optional.ofNullable(mockResource));

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getHeaders().get("Content-Type") != null || response.getHeaders().get("content-type") != null);
        assertNotNull(response.getHeaders().get("X-Powered-By"));
        JSONAssert.assertEquals(extractedResponseBody, response.getBody(), JSONCompareMode.STRICT);
    }

    @Test
    @SneakyThrows
    void testOK_parachuteRule() {

        // Generating variables
        String body = "{\"name\":\"another-ec\"}";
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(body, "POST", "ec-service/api/v1/organizations/77777777777", "application/json");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, body, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = "{\"organizationName\": \"parachute organization\",\"organizationOnboardingDate\": \"2023-06-20T15:03:56.862641\"}";

        // Mocking responses
        MockResourceEntity mockResource = new Gson().fromJson(TestUtil.readJsonFromFile("request/mock_resources/mock_resource_ok.json"), MockResourceEntity.class);
        when(repository.findById(anyString())).thenReturn(Optional.ofNullable(mockResource));

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getHeaders().get("Content-Type") != null || response.getHeaders().get("content-type") != null);
        assertNotNull(response.getHeaders().get("X-Powered-By"));
        JSONAssert.assertEquals(extractedResponseBody, response.getBody(), JSONCompareMode.STRICT);
    }

    @Test
    @SneakyThrows
    void testOK_notInjectedParam() {

        // Generating variables
        String body = "{\"name\":\"fake-ec\",\"uninjectable\":null}";
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(body, "POST", "ec-service/api/v1/organizations/77777777777", "application/json");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, body, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = "{\"organizationName\": \"\",\"organizationOnboardingDate\": \"2023-06-20T15:03:56.862641\"}";

        // Mocking responses
        MockResourceEntity mockResource = new Gson().fromJson(TestUtil.readJsonFromFile("request/mock_resources/mock_resource_ok_notinjection.json"), MockResourceEntity.class);
        when(repository.findById(anyString())).thenReturn(Optional.ofNullable(mockResource));

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getHeaders().get("Content-Type") != null || response.getHeaders().get("content-type") != null);
        assertNotNull(response.getHeaders().get("X-Powered-By"));
        JSONAssert.assertEquals(extractedResponseBody, response.getBody(), JSONCompareMode.STRICT);
    }

    @Test
    @SneakyThrows
    void testOK_noRule() {

        // Generating variables
        String body = "{\"name\":\"another-ec\"}";
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(body, "POST", "ec-service/api/v1/organizations/77777777777", "application/json");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, body, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = "{ \"message\": \"The passed request is not compliant with any rule for the found mock resource with URL [ec-service/api/v1/organizations/77777777777].\" }";

        // Mocking responses
        MockResourceEntity mockResource = new Gson().fromJson(TestUtil.readJsonFromFile("request/mock_resources/mock_resource_onlymainrule.json"), MockResourceEntity.class);
        when(repository.findById(anyString())).thenReturn(Optional.ofNullable(mockResource));

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(500, response.getStatus());
        JSONAssert.assertEquals(extractedResponseBody, response.getBody(), JSONCompareMode.STRICT);
    }

    @Test
    @SneakyThrows
    void testOK_ruleForURLCondition() {

        // Generating variables
        String body = "{\"name\":\"fake-ec\"}";
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(body, "POST", "ec-service/api/v1/organizations/77777777777", "application/json");
        httpServletRequest.addParameter("param", "value");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, body, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = "{\"organizationName\": \"fake-ec\",\"organizationOnboardingDate\": \"2023-06-20T15:03:56.862641\"}";

        // Mocking responses
        MockResourceEntity mockResource = new Gson().fromJson(TestUtil.readJsonFromFile("request/mock_resources/mock_resource_ok_urlcheck.json"), MockResourceEntity.class);
        when(repository.findById(anyString())).thenReturn(Optional.ofNullable(mockResource));

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getHeaders().get("Content-Type") != null || response.getHeaders().get("content-type") != null);
        assertNotNull(response.getHeaders().get("X-Powered-By"));
        JSONAssert.assertEquals(extractedResponseBody, response.getBody(), JSONCompareMode.STRICT);
    }

    @Test
    @SneakyThrows
    void testOK_otherContentType() {

        // Generating variables
        String body = "plain-string";
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(body, "POST", "ec-service/api/v1/organizations/77777777777", "text/some-exotic-type");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, body, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = "{\"message\": \"SOME PLAIN STRING!\"}";

        // Mocking responses
        MockResourceEntity mockResource = new Gson().fromJson(TestUtil.readJsonFromFile("request/mock_resources/mock_resource_ok_plainstring.json"), MockResourceEntity.class);
        when(repository.findById(anyString())).thenReturn(Optional.ofNullable(mockResource));

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getHeaders().get("Content-Type") != null || response.getHeaders().get("content-type") != null);
        assertNotNull(response.getHeaders().get("X-Powered-By"));
        JSONAssert.assertEquals(extractedResponseBody, response.getBody(), JSONCompareMode.STRICT);
    }

    @Test
    @SneakyThrows
    void testOK_xmlRequest() {

        // Generating variables
        String body = "<envelope><body><name>fake-ec</name></body></envelope>";
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(body, "POST", "ec-service/api/v1/organizations/77777777777", "application/xml");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, body, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = TestUtil.readXMLFromFile("response/service_generic.xml").trim();

        // Mocking responses
        MockResourceEntity mockResource = new Gson().fromJson(TestUtil.readJsonFromFile("request/mock_resources/mock_resource_xml_ok.json"), MockResourceEntity.class);
        when(repository.findById(anyString())).thenReturn(Optional.ofNullable(mockResource));

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getHeaders().get("Content-Type") != null || response.getHeaders().get("content-type") != null);
        assertNotNull(response.getHeaders().get("X-Powered-By"));
        assertEquals(extractedResponseBody, response.getBody());
    }


    @Test
    @SneakyThrows
    void testOK_nullBodyForNonNullCondition() {

        // Generating variables
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(null, "POST", "ec-service/api/v1/organizations/77777777777", "application/json");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, null, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = "{ \"message\": \"The passed request is not compliant with any rule for the found mock resource with URL [ec-service/api/v1/organizations/77777777777].\" }";

        // Mocking responses
        MockResourceEntity mockResource = new Gson().fromJson(TestUtil.readJsonFromFile("request/mock_resources/mock_resource_ok.json"), MockResourceEntity.class);
        when(repository.findById(anyString())).thenReturn(Optional.ofNullable(mockResource));

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(500, response.getStatus());
        assertTrue(response.getHeaders().get("Content-Type") != null || response.getHeaders().get("content-type") != null);
        assertEquals(extractedResponseBody, response.getBody());
    }

    @Test
    @SneakyThrows
    void testKO_unparseableXml() {

        // Generating variables
        String body = "<envelope><body><name>fake-ec</body></envelope>";
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(body, "POST", "ec-service/api/v1/organizations/77777777777", "application/xml");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, body, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = "<response><outcome>KO</outcome><message>An error occurred while parsing the request.</message></response>";

        // Mocking responses
        MockResourceEntity mockResource = new Gson().fromJson(TestUtil.readJsonFromFile("request/mock_resources/mock_resource_xml_ok.json"), MockResourceEntity.class);
        when(repository.findById(anyString())).thenReturn(Optional.ofNullable(mockResource));

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(500, response.getStatus());
        assertTrue(response.getHeaders().get("Content-Type") != null || response.getHeaders().get("content-type") != null);
        assertEquals(extractedResponseBody, response.getBody());
    }

    @Test
    @SneakyThrows
    void testKO_noResourceRegistered() {

        // Generating variables
        String body = "{\"name\":\"another-ec\"}";
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(body, "POST", "ec-service/api/v1/organizations/77777777777", "application/json");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, body, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = "{ \"message\": \"No valid mock resource is registered at URL [ec-service/api/v1/organizations/77777777777].\" }";

        // Mocking responses
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(500, response.getStatus());
        JSONAssert.assertEquals(extractedResponseBody, response.getBody(), JSONCompareMode.STRICT);
    }

    @Test
    @SneakyThrows
    void testKO_inactiveResource() {

        // Generating variables
        String body = "{\"name\":\"another-ec\"}";
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(body, "POST", "ec-service/api/v1/organizations/77777777777", "application/json");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, body, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = "{ \"message\": \"The mock resource registered at URL [ec-service/api/v1/organizations/77777777777] is currently disabled and cannot be accessed.\" }";


        // Mocking responses
        MockResourceEntity mockResource = new Gson().fromJson(TestUtil.readJsonFromFile("request/mock_resources/mock_resource_onlymainrule.json"), MockResourceEntity.class);
        mockResource.setIsActive(false);
        when(repository.findById(anyString())).thenReturn(Optional.ofNullable(mockResource));

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(500, response.getStatus());
        JSONAssert.assertEquals(extractedResponseBody, response.getBody(), JSONCompareMode.STRICT);
    }

    @Test
    @SneakyThrows
    void testKO_genericExceptionJSON() {

        // Generating variables
        String body = "{\"name\":\"another-ec\"}";
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(body, "POST", "ec-service/api/v1/organizations/77777777777", "application/json");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, body, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = "{ \"message\": \"An unexpected error occurred while searching for mocked resource.\" }";

        // Mocking responses
        when(repository.findById(anyString())).thenThrow(new DataRetrievalFailureException("some description"));

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(500, response.getStatus());
        JSONAssert.assertEquals(extractedResponseBody, response.getBody(), JSONCompareMode.STRICT);
    }

    @Test
    @SneakyThrows
    void testKO_genericExceptionXML() {

        // Generating variables
        String body = "<envelope><body>content</body></envelope>";
        MockHttpServletRequest httpServletRequest = getMockedHTTPServletRequest(body, "POST", "ec-service/api/v1/organizations/77777777777", "application/xml");
        ExtractedRequest extractedRequest = ExtractedRequest.extract(httpServletRequest, body, Set.of());
        extractedRequest.getHeaders().put("X-Client-Name", "pagopa");
        String extractedResponseBody = "<response><outcome>KO</outcome><message>An unexpected error occurred while searching for mocked resource.</message></response>";

        // Mocking responses
        when(repository.findById(anyString())).thenThrow(new DataRetrievalFailureException("some description"));

        // Executing logic
        ExtractedResponse response = service.analyze(extractedRequest);

        // Check assertions
        assertNotNull(response);
        assertEquals(500, response.getStatus());
        assertEquals(extractedResponseBody, response.getBody());
    }

    private MockHttpServletRequest getMockedHTTPServletRequest(String content, String method, String url, String contentType) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(content != null ? content.getBytes() : new byte[]{});
        request.setMethod(method);
        request.setContentType(contentType);
        request.setServerName("http://localhost");
        request.setRequestURI("/mocker" + url);
        return request;
    }
}
