package ik.mock.exceptions;

public class JsonResourceDeserializationException extends TestsCanBeExecutedException {
    public JsonResourceDeserializationException() {
    }

    public JsonResourceDeserializationException(String message) {
        super(message);
    }

    public JsonResourceDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonResourceDeserializationException(Throwable cause) {
        super(cause);
    }
}
