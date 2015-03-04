package org.wikiup.core.inf;

public interface Factory<T> {
    public T build(Document doc);
}
