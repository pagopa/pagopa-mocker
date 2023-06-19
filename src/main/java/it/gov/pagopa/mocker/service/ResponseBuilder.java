package it.gov.pagopa.mocker.service;

import it.gov.pagopa.mocker.misc.Constants;
import it.gov.pagopa.mocker.model.ExtractedRequest;
import it.gov.pagopa.mocker.model.ExtractedResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public class ResponseBuilder {

    private static Set<String> XML_FORMATS = Set.of(Constants.TEXT_XML, Constants.APPLICATION_XML);

    public static ExtractedResponse buildErrorResponse(String message, ExtractedRequest request) {
        log.error(String.format("An error occurred during mock retrieving: [%s].", message));
        String contentType = request.getContentType();
        String body = getDefaultMessageByContentType(message, contentType);
        Map<String, String> headers = request.getHeaders();
        headers.put(Constants.HEADER_CONTENTTYPE, contentType);
        return buildResponse(500, body, headers);
    }

    private static ExtractedResponse buildResponse(int httpStatus, String body, Map<String, String> headers) {
        return ExtractedResponse.builder().body(body).status(httpStatus).headers(headers).build();
    }

    private static String getDefaultMessageByContentType(String message, String contentType) {
        String body = message;
        if ("application/json".equals(contentType)) {
            body = "{ \"message\": \"" + message +"\" }";
        }
        else if (XML_FORMATS.contains(contentType)) {
            body = "<response><outcome>KO</outcome><message>" + message + "</message></response>";
        }
        return body;
    }
}
