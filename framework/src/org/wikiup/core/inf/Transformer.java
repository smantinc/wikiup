package org.wikiup.core.inf;

public interface Transformer<F, T> {
    public T transform(F from);
}
