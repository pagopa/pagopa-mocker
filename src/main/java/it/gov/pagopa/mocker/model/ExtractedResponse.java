package it.gov.pagopa.mocker.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class ExtractedResponse {
    private String body;
    private int status;
    private Map<String, String> headers;
}
