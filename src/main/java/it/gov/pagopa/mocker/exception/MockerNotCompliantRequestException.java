package it.gov.pagopa.mocker.exception;

public class MockerNotCompliantRequestException extends MockerException {

    public MockerNotCompliantRequestException(String url) {
        super(String.format("The passed request is not compliant with any rule for the found mock resource with URL [%s].", url));
    }
}
