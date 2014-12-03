package org.wikiup.core.exception;

public class AttributeException extends WikiupRuntimeException {
    static final long serialVersionUID = 1334009765210456052L;

    public AttributeException() {
        super();
    }

    public AttributeException(String message) {
        super(message);
    }

    public AttributeException(Object... args) {
        super("{0} has no attribute '{1}'", args);
    }
}
