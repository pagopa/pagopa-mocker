package it.gov.pagopa.mocker.exceptions;

public class MockerNotCompliantRequestException extends MockerException {

    public MockerNotCompliantRequestException(String url) {
        super(String.format("The passed request is not compliant with no rule for the found mock resource with URL [%s].", url));
    }
}
