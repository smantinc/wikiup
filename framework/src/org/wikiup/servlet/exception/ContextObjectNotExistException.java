package org.wikiup.servlet.exception;

public class ContextObjectNotExistException extends RuntimeException {
    static final long serialVersionUID = 8579941582052064946L;

    public ContextObjectNotExistException() {
        super();
    }

    public ContextObjectNotExistException(String message) {
        super(message);
    }

    public ContextObjectNotExistException(Throwable cause) {
        super(cause);
    }

    public ContextObjectNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
