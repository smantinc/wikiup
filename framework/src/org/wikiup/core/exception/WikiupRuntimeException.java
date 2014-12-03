package org.wikiup.core.exception;

import org.wikiup.core.util.StringUtil;

public class WikiupRuntimeException extends RuntimeException {
    public WikiupRuntimeException() {
        super();
    }

    public WikiupRuntimeException(Throwable cause) {
        super(cause);
        setStackTrace(cause.getStackTrace());
    }

    public WikiupRuntimeException(String message) {
        super(message);
    }

    public WikiupRuntimeException(String message, Object... args) {
        super(StringUtil.format(message, args));
    }
}
