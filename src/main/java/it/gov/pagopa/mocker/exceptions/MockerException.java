package it.gov.pagopa.mocker.exceptions;

public class MockerException extends Exception {

    private String message;

    public MockerException(String message) {
        super(message);
        this.message = message;
    }

    public MockerException(Exception e, String message) {
        super(message);
        this.initCause(e);
        this.message = message;
    }

    public String getErrorMessage() {
        return this.message;
    }
}
