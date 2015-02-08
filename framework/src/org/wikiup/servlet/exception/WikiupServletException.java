package org.wikiup.servlet.exception;

import org.wikiup.core.exception.WikiupRuntimeException;

public class WikiupServletException extends WikiupRuntimeException {
    private int errorCode;

    public WikiupServletException() {
        super();
    }

    public WikiupServletException(Throwable cause) {
        super(cause);
    }

    public WikiupServletException(String message) {
        super(message);
    }

    public WikiupServletException(int errorCode, String message, Object... args) {
        super(message, args);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
