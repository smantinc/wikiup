package org.wikiup.core.inf;

@Deprecated
public interface Filter<E, R> {
    public R filter(E object);
}
