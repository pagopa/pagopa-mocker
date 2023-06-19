package it.gov.pagopa.mocker.exceptions;

public class GenericMockerException extends MockerException {

    public GenericMockerException(String message) {
        super(message);
    }

    public GenericMockerException(Exception e, String message) {
        super(e, message);
    }
}
