package it.gov.pagopa.mocker.exception;

public class MockerScriptExecutionException extends MockerException {

    public MockerScriptExecutionException(Exception e, String scriptName) {
        super(e, String.format("The execution of the script with name [%s] ended with an error.", scriptName));
    }
}
