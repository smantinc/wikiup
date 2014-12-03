package org.wikiup.servlet.exception;

public class ServiceNotImplementException extends RuntimeException {
    static final long serialVersionUID = 4407814762631671934L;

    public ServiceNotImplementException() {
        super();
    }

    public ServiceNotImplementException(String message) {
        super(message);
    }
}
