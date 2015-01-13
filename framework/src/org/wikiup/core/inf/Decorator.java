package org.wikiup.core.inf;

public interface Decorator<T> {
    public T decorate(T obj, Document desc);
}
