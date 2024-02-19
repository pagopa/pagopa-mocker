package it.gov.pagopa.mocker.exception;

import it.gov.pagopa.mocker.model.ExtractedRequest;

public class MockerNotRegisteredException extends MockerException {

    public MockerNotRegisteredException(ExtractedRequest extractedRequest) {
        super(String.format("No valid mock resource is registered as: HTTP Method [%s] - URL [%s] - Special Headers [%s].", extractedRequest.getMethod(), extractedRequest.getUrl(), extractedRequest.getSpecialHeaders()));
    }
}
