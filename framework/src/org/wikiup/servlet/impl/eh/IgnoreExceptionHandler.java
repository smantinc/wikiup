package org.wikiup.servlet.impl.eh;

import org.wikiup.core.inf.ExceptionHandler;

public class IgnoreExceptionHandler implements ExceptionHandler {
    public boolean handle(Exception exp) {
        return true;
    }
}
