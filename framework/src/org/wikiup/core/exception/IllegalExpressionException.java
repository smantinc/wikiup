package org.wikiup.core.exception;

public class IllegalExpressionException extends RuntimeException {
    static final long serialVersionUID = 1114537024472919078L;

    public IllegalExpressionException() {
        super();
    }

    public IllegalExpressionException(String message) {
        super(message);
    }

    public IllegalExpressionException(Throwable cause) {
        super(cause);
    }

    public IllegalExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

}
