package it.gov.pagopa.mocker.exceptions;

public class MockerParseRequestException extends MockerException {

    public MockerParseRequestException(Exception e) {
        super(e, "An error occurred while parsing the request.");
    }
}
