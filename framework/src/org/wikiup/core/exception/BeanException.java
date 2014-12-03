package org.wikiup.core.exception;

public class BeanException extends WikiupRuntimeException {
    static final long serialVersionUID = -7351327227066120531L;

    public BeanException() {
        super();
    }

    public BeanException(String message) {
        super(message);
    }

    public BeanException(Object... args) {
        super("Cannot initialize bean '{0}' defined in {1}", args);
    }
}
