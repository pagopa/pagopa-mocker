package it.gov.pagopa.mocker.model;

import it.gov.pagopa.mocker.util.Constants;
import it.gov.pagopa.mocker.util.Utility;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Getter
@Slf4j
public class ExtractedRequest {

    private final String id;

    private final String cacheId;

    private final String url;

    private final String contentType;

    private final String body;

    private final Map<String, String> headers;

    private final Map<String, String> queryParameters;

    private ExtractedRequest(HttpServletRequest request, String body) {
        this.url = request.getRequestURI().replace(Constants.MOCKER_PATH_ROOT, Constants.EMPTY_STRING);
        this.id = Utility.generateHash(this.url, request.getMethod().toLowerCase());
        this.body = body;
        this.headers = extractHeaders(request);
        this.contentType = (this.headers.isEmpty() || this.headers.get(Constants.HEADER_CONTENTTYPE) == null) ? Constants.APPLICATION_JSON : this.headers.get(Constants.HEADER_CONTENTTYPE);
        this.queryParameters = extractQueryParameters(request);
        String soapAction = (this.headers.isEmpty() || this.headers.get(Constants.HEADER_SOAPACTION) == null) ? Constants.EMPTY_STRING : this.headers.get(Constants.HEADER_SOAPACTION).toLowerCase();
        this.cacheId = Utility.generateHash(this.url, soapAction, request.getMethod().toLowerCase());
    }

    public static ExtractedRequest extract(HttpServletRequest request, String body) {
        ExtractedRequest extractedData = new ExtractedRequest(request, body);
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
        return String.format("[Extracted ID: %s, Content-Type: %s, Request body: %s]", this.id, this.contentType, this.body);
    }
}
