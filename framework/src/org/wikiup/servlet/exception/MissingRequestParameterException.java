package org.wikiup.servlet.exception;

public class MissingRequestParameterException extends RuntimeException {
    static final long serialVersionUID = 1527956842575948520L;

    public MissingRequestParameterException() {
        super();
    }

    public MissingRequestParameterException(String message) {
        super(message);
    }

    public MissingRequestParameterException(Throwable cause) {
        super(cause);
    }

    public MissingRequestParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
