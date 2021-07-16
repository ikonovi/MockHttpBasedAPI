package ik.mock.exceptions;

public class MappingHttpServiceException extends TestsExecutionException {
    public MappingHttpServiceException() {
    }

    public MappingHttpServiceException(String message) {
        super(message);
    }

    public MappingHttpServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingHttpServiceException(Throwable cause) {
        super(cause);
    }
}
