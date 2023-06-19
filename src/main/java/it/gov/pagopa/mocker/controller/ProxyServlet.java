package it.gov.pagopa.mocker.controller;

import it.gov.pagopa.mocker.misc.Constants;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ProxyServlet extends HttpServlet {

    @Value("${application.name}")
    private String name;

    @Value("${application.version}")
    private String version;

    @Value("${application.environment}")
    private String environment;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI().replace(Constants.MOCKER_PATH_ROOT, "");
        if ("/info".equals(url)) {
            getAppInfo(req, resp);
        } else {
            analyzeRequest(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        analyzeRequest(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        analyzeRequest(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        analyzeRequest(req, resp);
    }

    private void getAppInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType(Constants.APPLICATION_JSON);
        AppInfo info = AppInfo.builder()
                .name(name)
                .version(version)
                .environment(environment)
                .dbConnection(healthCheckService.checkDBConnection() ? "up" : "down")
                .build();
        response.getWriter().append(info.toString());
    }

    private void analyzeRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /* Extract body from HttpServletRequest request and analyze all. */
        long start = System.nanoTime();
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        log.info(String.format("Trying to mocking the response for the called resource: [%s]", request.getRequestURI().replace(Constants.MOCKER_PATH_ROOT, "")));
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
    }
}
