package org.wikiup.core.impl.translator.lf;

import org.wikiup.core.inf.ext.LogicalTranslator;

public class NotLogicalTranslator<E> implements LogicalTranslator<E> {
    private LogicalTranslator<E> filter;

    public NotLogicalTranslator(LogicalTranslator<E> filter) {
        this.filter = filter;
    }

    public Boolean translate(E object) {
        return !filter.translate(object);
    }
}
