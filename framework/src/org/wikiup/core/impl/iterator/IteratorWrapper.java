package org.wikiup.core.impl.iterator;

import java.util.Iterator;

public class IteratorWrapper<E> implements Iterator<E> {
    private Iterator<E> iterator;

    public IteratorWrapper(Iterator<E> iteartor) {
        this.iterator = iteartor;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public E next() {
        return iterator.next();
    }

    public void remove() {
        iterator.remove();
    }
}
