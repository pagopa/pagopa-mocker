package it.gov.pagopa.mocker.exception;

public class MockerPersistenceException extends MockerException {

    public MockerPersistenceException(Exception e) {
        super(e, "An error occurred while read data.");
    }
}
