package it.gov.pagopa.mocker.controller;

import it.gov.pagopa.mocker.util.Constants;
import it.gov.pagopa.mocker.model.AppInfo;
import it.gov.pagopa.mocker.model.ExtractedRequest;
import it.gov.pagopa.mocker.model.ExtractedResponse;
import it.gov.pagopa.mocker.service.HealthCheckService;
import it.gov.pagopa.mocker.service.MockerService;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ProxyServlet extends HttpServlet {

    @Autowired
    private Environment env;

    @Autowired
    private MockerService mockerService;

    @Autowired
    private HealthCheckService healthCheckService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String url = req.getRequestURI();
        if ("/info".equals(url)) {
            getAppInfo(resp);
        } else {
            analyzeRequest(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        analyzeRequest(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        analyzeRequest(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        analyzeRequest(req, resp);
    }

    private void getAppInfo(HttpServletResponse response)  {
        try {
            response.setStatus(200);
            response.setContentType(Constants.APPLICATION_JSON);
            AppInfo info = AppInfo.builder()
                    .name(env.getProperty("application.name"))
                    .version(env.getProperty("application.version"))
                    .environment(env.getProperty("application.environment"))
                    .dbConnection(healthCheckService.checkDBConnection() ? "up" : "down")
                    .build();
            response.getWriter().append(info.toString());
        } catch (IOException e) {
            log.error("Something failed during writing response for proxy servlet.", e);
        }
    }

    private void analyzeRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            /* Extract body from HttpServletRequest request and analyze all. */
            long start = System.nanoTime();
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            log.info(String.format("Trying to mocking the response for the called resource: [%s]", request.getRequestURI()));
            ExtractedResponse extractedResponse = mockerService.analyze(ExtractedRequest.extract(request, body));
            /* Update HttpServletResponse response */
            response.setStatus(extractedResponse.getStatus());
            response.setCharacterEncoding("UTF-8");
            response.setContentType(extractedResponse.getHeaders().get("content-type"));
            for (Map.Entry<String, String> headerPair : extractedResponse.getHeaders().entrySet()) {
                response.setHeader(headerPair.getKey(), headerPair.getValue());
            }
            PrintWriter writer = response.getWriter();
            writer.print(extractedResponse.getBody());
            writer.flush();
            log.info(String.format("The generation of mock response ended in [%s] ms", (System.nanoTime() - start) / 1000000 ));
        } catch (IOException e) {
            log.error("Something failed during writing response for proxy servlet.", e);
        }
    }
}
