package it.gov.pagopa.mocker.controller;

import it.gov.pagopa.mocker.MockerApplication;
import it.gov.pagopa.mocker.model.AppInfo;
import it.gov.pagopa.mocker.model.ExtractedRequest;
import it.gov.pagopa.mocker.model.ExtractedResponse;
import it.gov.pagopa.mocker.service.HealthCheckService;
import it.gov.pagopa.mocker.service.MockerService;
import it.gov.pagopa.mocker.service.ProxyService;
import it.gov.pagopa.mocker.utility.TestUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MockerApplication.class)
@AutoConfigureMockMvc
class ProxyServletTest {

    @InjectMocks
    private ProxyServlet servlet;

    @MockBean
    private ProxyService proxyService;

    @MockBean
    private MockerService mockerService;

    @MockBean
    private HealthCheckService healthCheckService;


    @BeforeEach
    @SneakyThrows
    void setup() {
        MockitoAnnotations.openMocks(this);
        // set parameters
        ReflectionTestUtils.setField(servlet, "acceptedSpecialHeaders", new HashSet<>());
        ReflectionTestUtils.setField(servlet, "acceptedClients", new HashSet<>());
    }

    @Test
    @SneakyThrows
    void testInfo() {

        // Mocking objects
        doReturn(AppInfo.builder()
                .name("mocker")
                .version("x.y.z")
                .environment("test")
                .dbConnection("up")
                .redisConnection("up")
                .build()
        ).when(healthCheckService).getAppInfo();
        MockHttpServletRequest request = getMockedHTTPServletRequest(null, "GET", "/info", "application/json");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Executing logic
        servlet.service(request, response);

        // Analyzing assertions
        String expected = TestUtil.readJsonFromFile("response/service_info_ok.json");
        JSONAssert.assertEquals(expected, response.getContentAsString(), JSONCompareMode.STRICT);
        assertEquals(200, response.getStatus());
    }

    @Test
    @SneakyThrows
    void testInfo_dbKO() {

        // Mocking objects
        doReturn(AppInfo.builder()
                .name("mocker")
                .version("x.y.z")
                .environment("test")
                .dbConnection("down")
                .redisConnection("down")
                .build()
        ).when(healthCheckService).getAppInfo();
        MockHttpServletRequest request = getMockedHTTPServletRequest(null, "GET", "/info", "application/json");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Executing logic
        servlet.service(request, response);

        // Analyzing assertions
        String expected = TestUtil.readJsonFromFile("response/service_info_ko.json");
        JSONAssert.assertEquals(expected, response.getContentAsString(), JSONCompareMode.STRICT);
        assertEquals(200, response.getStatus());
    }

    @Test
    @SneakyThrows
    void testGetJSON() {

        // Mocking objects
        doReturn(getMockedResponseFromJSON("response/service_get.json", 200)).when(mockerService).analyze(any(ExtractedRequest.class));
        MockHttpServletRequest request = getMockedHTTPServletRequest(null, "GET", "/someresource", "application/json");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Executing logic
        servlet.service(request, response);

        // Analyzing assertions
        String expected = TestUtil.readJsonFromFile("response/service_get.json");
        JSONAssert.assertEquals(expected, new String(Base64.getDecoder().decode(response.getContentAsString())), JSONCompareMode.STRICT);
        assertEquals(200, response.getStatus());
    }

    @Test
    @SneakyThrows
    void testGetXML() throws Exception {

        // Mocking objects
        doReturn(getMockedResponseFromXML("response/service_get.xml", 200)).when(mockerService).analyze(any(ExtractedRequest.class));
        MockHttpServletRequest request = getMockedHTTPServletRequest(null, "GET", "/someresource", "application/xml");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Executing logic
        servlet.service(request, response);

        // Analyzing assertions
        String expected = TestUtil.readXMLFromFile("response/service_get.xml").trim();
        assertEquals(expected, new String(Base64.getDecoder().decode(response.getContentAsString())));
        assertEquals(200, response.getStatus());
    }

    @Test
    @SneakyThrows
    void testPostJSON() {

        // Mocking objects
        doReturn(getMockedResponseFromJSON("response/service_post.json", 201)).when(mockerService).analyze(any(ExtractedRequest.class));
        MockHttpServletRequest request = getMockedHTTPServletRequest("request/servlet_post.json", "POST", "/someresource", "application/json");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Executing logic
        servlet.service(request, response);

        // Analyzing assertions
        String expected = TestUtil.readJsonFromFile("response/service_post.json");
        JSONAssert.assertEquals(expected, new String(Base64.getDecoder().decode(response.getContentAsString())), JSONCompareMode.STRICT);
        assertEquals(201, response.getStatus());
    }

    @Test
    @SneakyThrows
    void testPostXML() {

        // Mocking objects
        ExtractedResponse mockedResponse = getMockedResponseFromXML("response/service_post.xml", 201);
        doReturn(mockedResponse).when(proxyService).extract(any(ExtractedRequest.class));
        MockHttpServletRequest request = getMockedHTTPServletRequest("request/servlet_post.json", "POST", "/someresource", "application/xml");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Executing logic
        servlet.service(request, response);

        // Analyzing assertions
        String expected = TestUtil.readXMLFromFile("response/service_post.xml").trim();
        assertEquals(expected, new String(Base64.getDecoder().decode(response.getContentAsString())));
        assertEquals(201, response.getStatus());
    }

    @Test
    @SneakyThrows
    void testPutJSON() {

        // Mocking objects
        doReturn(getMockedResponseFromJSON("response/service_put.json", 200)).when(mockerService).analyze(any(ExtractedRequest.class));
        MockHttpServletRequest request = getMockedHTTPServletRequest("request/servlet_put.json", "PUT", "/someresource/resourceId", "application/json");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Executing logic
        servlet.service(request, response);

        // Analyzing assertions
        String expected = TestUtil.readJsonFromFile("response/service_put.json");
        JSONAssert.assertEquals(expected, new String(Base64.getDecoder().decode(response.getContentAsString())), JSONCompareMode.STRICT);
        assertEquals(200, response.getStatus());
    }

    @Test
    @SneakyThrows
    void testPutXML() {

        // Mocking objects
        doReturn(getMockedResponseFromXML("response/service_put.xml", 200)).when(mockerService).analyze(any(ExtractedRequest.class));
        MockHttpServletRequest request = getMockedHTTPServletRequest("request/servlet_put.xml", "PUT", "/someresource/resourceId", "application/json");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Executing logic
        servlet.service(request, response);

        // Analyzing assertions
        String expected = TestUtil.readXMLFromFile("response/service_put.xml").trim();
        assertEquals(expected, new String(Base64.getDecoder().decode(response.getContentAsString())));
        assertEquals(200, response.getStatus());
    }

    @Test
    @SneakyThrows
    void testDeleteJSON() {

        // Mocking objects
        doReturn(getMockedResponseFromJSON(null, 204)).when(mockerService).analyze(any(ExtractedRequest.class));
        MockHttpServletRequest request = getMockedHTTPServletRequest(null, "DELETE", "/someresource/resourceId", "application/json");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Executing logic
        servlet.service(request, response);

        // Analyzing assertions
        assertEquals(204, response.getStatus());
    }

    @Test
    @SneakyThrows
    void testDeleteXML() {

        // Mocking objects
        doReturn(getMockedResponseFromXML(null, 204)).when(mockerService).analyze(any(ExtractedRequest.class));
        MockHttpServletRequest request = getMockedHTTPServletRequest(null, "DELETE", "/someresource/resourceId", "application/xml");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Executing logic
        servlet.service(request, response);

        // Analyzing assertions
        assertEquals(204, response.getStatus());
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

    private ExtractedResponse getMockedResponseFromJSON(String filename, int status) throws IOException {
        String body = null;
        if (filename != null) {
            body = TestUtil.readJsonFromFile(filename);
        }
        return getMockedResponse(body, status, "application/json");
    }

    private ExtractedResponse getMockedResponseFromXML(String filename, int status) throws IOException {
        String body = null;
        if (filename != null) {
            body = TestUtil.readJsonFromFile(filename);
        }
        return getMockedResponse(body, status, "application/xml");
    }

    private ExtractedResponse getMockedResponse(String content, int status, String contentType) {
        String base64Content = content != null ? new String(Base64.getEncoder().encode(content.getBytes())) : null;
        return ExtractedResponse.builder()
                .body(base64Content)
                .status(status)
                .headers(Map.of(
                        "Content-Type", contentType,
                        "X-Powered-By", "Mocker"
                ))
                .build();
    }

}
