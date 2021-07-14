package ik.mock.exceptions;

public class TestsCanBeExecutedException extends Exception{
    public TestsCanBeExecutedException() {
    }

    public TestsCanBeExecutedException(String message) {
        super(message);
    }

    public TestsCanBeExecutedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestsCanBeExecutedException(Throwable cause) {
        super(cause);
    }
}
