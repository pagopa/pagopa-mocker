package it.gov.pagopa.mocker.exception;

public class MockerInitializationException extends MockerException {

    public MockerInitializationException(Exception e) {
        super(e, "An error occurred while intializing Mocker system.");
    }
}
