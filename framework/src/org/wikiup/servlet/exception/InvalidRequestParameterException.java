package org.wikiup.servlet.exception;

public class InvalidRequestParameterException extends RuntimeException {
    static final long serialVersionUID = 8179184787521908604L;

    public InvalidRequestParameterException() {
        super();
    }

    public InvalidRequestParameterException(String message) {
        super(message);
    }

    public InvalidRequestParameterException(Throwable cause) {
        super(cause);
    }

    public InvalidRequestParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
