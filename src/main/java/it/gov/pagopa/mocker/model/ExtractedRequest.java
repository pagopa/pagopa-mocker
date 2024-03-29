package it.gov.pagopa.mocker.model;

import it.gov.pagopa.mocker.util.Constants;
import it.gov.pagopa.mocker.util.Utility;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Getter
@Slf4j
public class ExtractedRequest {

    private final String id;

    private final String method;

    private String url;

    private final String contentType;

    private final String body;

    private final String specialHeaders;

    private final Map<String, String> headers;

    private final Map<String, String> queryParameters;

    private ExtractedRequest(HttpServletRequest request, String body, Set<String> acceptedSpecialHeaders) {
        this.url = request.getRequestURI().replace(Constants.MOCKER_PATH_ROOT, Constants.EMPTY_STRING);
        if (!this.url.endsWith("/")) {
            this.url = this.url.concat("/");
        }
        this.method = request.getMethod().toLowerCase();
        this.body = body;
        this.headers = extractHeaders(request);
        this.specialHeaders = extractSpecialHeaders(headers, acceptedSpecialHeaders);
        this.id = Utility.generateHash(method, this.url, specialHeaders);
        this.contentType = (this.headers.isEmpty() || this.headers.get(Constants.HEADER_CONTENTTYPE) == null) ? Constants.APPLICATION_JSON : this.headers.get(Constants.HEADER_CONTENTTYPE);
        this.queryParameters = extractQueryParameters(request);
    }

    public static ExtractedRequest extract(HttpServletRequest request, String body, Set<String> acceptedSpecialHeaders) {
        ExtractedRequest extractedData = new ExtractedRequest(request, body, acceptedSpecialHeaders);
        log.debug(String.format("Analyzing the following request: %s", extractedData));
        return extractedData;
    }

    private static Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> extractedHeaders = new HashMap<>();
        Iterator<String> it = request.getHeaderNames().asIterator();
        while (it.hasNext()) {
            String headerName = it.next();
            if (!Constants.HEADER_CONTENTLENGTH.equals(headerName)) {
                extractedHeaders.put(headerName.toLowerCase(), request.getHeader(headerName));
            }
        }
        return extractedHeaders;
    }

    private static String extractSpecialHeaders(Map<String, String> headers, Set<String> acceptedSpecialHeaders) {
        List<String> specialHeaders = new LinkedList<>();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            String headerKey = header.getKey();
            if (acceptedSpecialHeaders.contains(header.getKey().toLowerCase())) {
                String headerValue = Utility.deEscapeString(headers.get(headerKey)).toLowerCase();
                specialHeaders.add(headerKey.trim().toLowerCase() + ":" + headerValue.toLowerCase());
            }
        }
        StringJoiner specialHeadersBuilder = new StringJoiner(";");
        specialHeaders.stream()
                .sorted()
                .forEach(specialHeadersBuilder::add);
        return specialHeadersBuilder.toString();
    }

    private static Map<String, String> extractQueryParameters(HttpServletRequest request) {
        Map<String, String> extractedQueryParameters = new HashMap<>();
        Iterator<String> it = request.getParameterNames().asIterator();
        while (it.hasNext()) {
            String parameterName = it.next();
            extractedQueryParameters.put(parameterName, request.getParameter(parameterName));
        }
        return extractedQueryParameters;
    }

    @Override
    public String toString() {
        return String.format("[Extracted ID: %s, URL: %s, Method: %s, Special headers: %s, Content-Type: %s, Request body: %s]", this.id, this.url, this.method, this.specialHeaders, this.contentType, this.body);
    }
}
