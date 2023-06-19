package it.gov.pagopa.mocker.exceptions;

public class MockerInvalidConfigurationException extends MockerException {

    public MockerInvalidConfigurationException(String id) {
        super(String.format("No valid 'condition type' field defined in the mock condition with id [%s]", id));
    }
}
