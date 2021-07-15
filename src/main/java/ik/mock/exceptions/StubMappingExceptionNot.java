package ik.mock.exceptions;

public class StubMappingExceptionNot extends TestsExecutionException {
    public StubMappingExceptionNot() {
    }

    public StubMappingExceptionNot(String message) {
        super(message);
    }

    public StubMappingExceptionNot(String message, Throwable cause) {
        super(message, cause);
    }

    public StubMappingExceptionNot(Throwable cause) {
        super(cause);
    }
}
