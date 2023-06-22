package it.gov.pagopa.mocker.exception;

public class MockerParseRequestException extends MockerException {

    public MockerParseRequestException(Exception e) {
        super(e, "An error occurred while parsing the request.");
    }
}
