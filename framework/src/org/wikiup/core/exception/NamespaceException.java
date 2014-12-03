package org.wikiup.core.exception;

public class NamespaceException extends RuntimeException {
    static final long serialVersionUID = -4256053659908927388L;

    public NamespaceException() {
        super();
    }

    public NamespaceException(String message) {
        super(message);
    }
}
