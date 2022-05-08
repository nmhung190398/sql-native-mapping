package net.devnguyen.tuplemapping;

public class TupleMappingException extends RuntimeException{
    public TupleMappingException() {
    }

    public TupleMappingException(String message) {
        super(message);
    }

    public TupleMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public TupleMappingException(Throwable cause) {
        super(cause);
    }

    public TupleMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
