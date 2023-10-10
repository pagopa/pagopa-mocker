package it.gov.pagopa.mocker.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@Builder
public class ExtractedResponse implements Serializable {
    private boolean isCacheable;
    private String body;
    private int status;
    private Map<String, String> headers;
}
