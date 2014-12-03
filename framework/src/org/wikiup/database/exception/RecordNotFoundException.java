package org.wikiup.database.exception;

public class RecordNotFoundException extends RuntimeException {
    static final long serialVersionUID = -6122508637059292351L;

    public RecordNotFoundException() {
        super();
    }

    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException(Throwable cause) {
        super(cause);
    }

    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
