package org.wikiup.modules.worms.exception;

public class GenericSQLException extends RuntimeException {
    static final long serialVersionUID = -5802238450840952799L;

    public GenericSQLException() {
        super();
    }

    public GenericSQLException(String message) {
        super(message);
    }

    public GenericSQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenericSQLException(Throwable cause) {
        super(cause);
    }
}
