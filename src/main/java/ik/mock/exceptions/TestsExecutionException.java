package ik.mock.exceptions;

public class TestsExecutionException extends Exception {
    public TestsExecutionException() {
    }

    public TestsExecutionException(String message) {
        super(message);
    }

    public TestsExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestsExecutionException(Throwable cause) {
        super(cause);
    }
}
