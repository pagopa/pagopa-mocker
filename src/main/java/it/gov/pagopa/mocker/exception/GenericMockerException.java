package it.gov.pagopa.mocker.exception;

public class GenericMockerException extends MockerException {

    public GenericMockerException(String message) {
        super(message);
    }

    public GenericMockerException(Exception e, String message) {
        super(e, message);
    }
}
