package org.wikiup.core.inf.ext;

import org.wikiup.core.inf.Provider;

public interface Container<E> extends Provider<E> {
    public void put(E instance);
}
