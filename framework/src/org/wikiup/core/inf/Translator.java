package org.wikiup.core.inf;

public interface Translator<E, R> {
    public R translate(E object);
}
