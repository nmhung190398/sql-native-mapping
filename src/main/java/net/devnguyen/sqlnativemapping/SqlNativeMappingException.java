package net.devnguyen.sqlnativemapping;

public class SqlNativeMappingException extends RuntimeException{
    public SqlNativeMappingException() {
    }

    public SqlNativeMappingException(String message) {
        super(message);
    }

    public SqlNativeMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlNativeMappingException(Throwable cause) {
        super(cause);
    }

    public SqlNativeMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
