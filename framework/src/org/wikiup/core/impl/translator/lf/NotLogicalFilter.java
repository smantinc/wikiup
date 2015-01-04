package org.wikiup.core.impl.translator.lf;

import org.wikiup.core.inf.ext.LogicalFilter;

public class NotLogicalFilter<E> implements LogicalFilter<E> {
    private LogicalFilter<E> filter;

    public NotLogicalFilter(LogicalFilter<E> filter) {
        this.filter = filter;
    }

    public Boolean translate(E object) {
        return !filter.translate(object);
    }
}
