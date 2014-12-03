package org.wikiup.core.impl.filter.lf;

import org.wikiup.core.inf.ext.LogicalFilter;

public class NotLogicalFilter<E> implements LogicalFilter<E> {
    private LogicalFilter<E> filter;

    public NotLogicalFilter(LogicalFilter<E> filter) {
        this.filter = filter;
    }

    public Boolean filter(E object) {
        return !filter.filter(object);
    }
}
