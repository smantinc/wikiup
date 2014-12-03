package org.wikiup.core.exception;

public class MalformedTemplateException extends RuntimeException {
    static final long serialVersionUID = -1812002948108045530L;

    public MalformedTemplateException() {
        super();
    }

    public MalformedTemplateException(String message) {
        super(message);
    }
}
