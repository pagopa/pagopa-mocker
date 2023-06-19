package it.gov.pagopa.mocker.exceptions;

public class MockerPersistenceException extends MockerException {

    public MockerPersistenceException(Exception e) {
        super(e, "An error occurred while read data.");
    }
}
