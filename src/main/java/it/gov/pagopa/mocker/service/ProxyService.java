package it.gov.pagopa.mocker.service;

import it.gov.pagopa.mocker.model.ExtractedRequest;
import it.gov.pagopa.mocker.model.ExtractedResponse;
import it.gov.pagopa.mocker.util.Constants;
import it.gov.pagopa.mocker.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProxyService {

    @Autowired
    private MockerService mockerService;

    @Autowired
    private CacheService cacheService;

    public ExtractedResponse extract(ExtractedRequest extractedRequest) {

        String hashedID = extractedRequest.getId();
        String headers = extractHeaderSubstring(extractedRequest.getHeaders());
        String queryParams = extractQueryParameterSubstring(extractedRequest.getQueryParameters());
        String body = extractedRequest.getBody();
        String hashedRequest = Utility.generateHash(headers, queryParams, body);

        ExtractedResponse response = cacheService.get(hashedID, hashedRequest);
        if (response == null) {
            response = mockerService.analyze(extractedRequest);
            if (response != null && response.isCacheable()) {
                cacheService.set(hashedID, hashedRequest, response);
            }
        }
        return response;
    }

    private String extractHeaderSubstring(Map<String, String> headers) {
        List<String> formattedHeaders = headers.keySet()
                .stream()
                .sorted()
                .filter(headerKey -> !Constants.NOT_CACHEABLE_HEADERS.contains(headerKey))
                .map(headerKey -> String.format("\"%s\":\"%s\"", headerKey, headers.get(headerKey)))
                .collect(Collectors.toList());
        return "headers:" + formattedHeaders.toString() + ";";
    }

    private String extractQueryParameterSubstring(Map<String, String> queryParameters) {
        List<String> formattedQueryParameters = queryParameters.keySet()
                .stream()
                .sorted()
                .filter(queryParamKey -> !Utility.isNullOrEmpty(queryParameters.get(queryParamKey)))
                .map(queryParamKey -> String.format("\"%s\":\"%s\"", queryParamKey, queryParameters.get(queryParamKey)))
                .collect(Collectors.toList());
        return "queryparams:" + formattedQueryParameters.toString() + ";";
    }

}
