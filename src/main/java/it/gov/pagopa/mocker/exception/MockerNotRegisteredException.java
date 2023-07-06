package it.gov.pagopa.mocker.exception;

public class MockerNotRegisteredException extends MockerException {

    public MockerNotRegisteredException(String url) {
        super(String.format("No valid mock resource is registered at URL [%s].", url));
    }
}
