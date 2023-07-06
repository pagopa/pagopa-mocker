package it.gov.pagopa.mocker.exception;

public class MockerNotActiveException extends MockerException {

    public MockerNotActiveException(String url) {
        super(String.format("The mock resource registered at URL [%s] is currently disabled and cannot be accessed.", url));
    }
}
