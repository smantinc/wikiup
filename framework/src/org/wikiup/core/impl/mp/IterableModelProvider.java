package org.wikiup.core.impl.mp;

import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.util.Interfaces;

import java.util.Iterator;

public class IterableModelProvider implements ModelProvider {

    private Iterable<?> iterable;

    public IterableModelProvider(Iterable<?> iterable) {
        this.iterable = iterable;
    }

    public <E> E getModel(Class<E> clazz) {
        Object model = Iterator.class.isAssignableFrom(clazz) ? iterable.iterator() : iterable;
        return Interfaces.cast(clazz, model);
    }
}
