package org.wikiup.database.exception;

import org.wikiup.core.exception.WikiupRuntimeException;

public class InsufficientPrimaryKeys extends WikiupRuntimeException {
    public InsufficientPrimaryKeys() {
        super();
    }

    public InsufficientPrimaryKeys(String message) {
        super(message);
    }

    public InsufficientPrimaryKeys(Object... args) {
        super("'{0}' has no primary keys[{1}], or didn't provide one in a SQL statement", args);
    }
}
