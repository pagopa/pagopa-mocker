package it.gov.pagopa.mocker.controller;

import it.gov.pagopa.mocker.service.ProxyService;
import it.gov.pagopa.mocker.util.Constants;
import it.gov.pagopa.mocker.model.AppInfo;
import it.gov.pagopa.mocker.model.ExtractedRequest;
import it.gov.pagopa.mocker.model.ExtractedResponse;
import it.gov.pagopa.mocker.service.HealthCheckService;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ProxyServlet extends HttpServlet {

    @Autowired
    private ProxyService proxyService;

    @Autowired
    private HealthCheckService healthCheckService;

    @Value("${mocker.request.accepted-special-headers}")
    private HashSet<String> acceptedSpecialHeaders;

    @Value("${mocker.request.accepted-clients}")
    private HashSet<String> acceptedClients;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        boolean analyzeRequest = true;
        if (method.equals("GET") && req.getRequestURI().startsWith(Constants.MOCKER_PATH_ROOT + "/info")) {
            analyzeRequest = false;
            getAppInfo(resp);
            addCorsAllowHeaders(resp);
        } else if (method.equals("OPTIONS")) {
            analyzeRequest = false;
            super.doOptions(req, resp);
            addCorsAllowHeaders(resp);
        }

        if (analyzeRequest) {
            analyzeRequest(req, resp);
        }
    }

    private void getAppInfo(HttpServletResponse response)  {
        try {
            response.setStatus(200);
            response.setContentType(Constants.APPLICATION_JSON);
            AppInfo info = healthCheckService.getAppInfo();
            response.getWriter().append(info.toString());
        } catch (IOException e) {
            log.error("Something failed during writing response for proxy servlet.", e);
        }
    }

    private void analyzeRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            /* Extract body from HttpServletRequest request and analyze all. */
            long start = System.nanoTime();
            log.info(String.format("Trying to mocking the response for the called resource: [%s]", request.getRequestURI()));
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            /* Extracting mock response */
            ExtractedRequest extractedRequest = ExtractedRequest.extract(request, body, acceptedSpecialHeaders);
            ExtractedResponse extractedResponse = proxyService.extract(extractedRequest);
            /* Update HttpServletResponse response */
            response.setStatus(extractedResponse.getStatus());
            response.setCharacterEncoding("UTF-8");
            for (Map.Entry<String, String> headerPair : extractedResponse.getHeaders().entrySet()) {
                response.setHeader(headerPair.getKey(), headerPair.getValue());
            }
            addContentType(response, extractedRequest, extractedResponse);
            response.setHeader("X-Powered-By", "Mocker");
            if (acceptedClients.contains(extractedRequest.getHeaders().get("x-source-client"))) {
                addCorsAllowHeaders(response);
            }
            PrintWriter writer = response.getWriter();
            writer.print(extractedResponse.getBody());
            writer.flush();
            log.info(String.format("The generation of mock response ended in [%s] ms", (System.nanoTime() - start) / 1000000 ));
        } catch (IOException e) {
            log.error("Something failed during writing response for proxy servlet.", e);
        }
    }

    private void addCorsAllowHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Expose-Headers", "*");
    }

    private void addContentType(HttpServletResponse response, ExtractedRequest extractedRequest, ExtractedResponse extractedResponse) {
        String contentType = extractedResponse.getHeaders().get("content-type");
        if (contentType == null) {
            contentType = extractedRequest.getContentType();
        }
        response.setContentType(contentType);
    }
}
