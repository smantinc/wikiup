package org.wikiup.core.impl.iterator;

import java.util.Iterator;

public class BufferedIterator<E> implements Iterator<E> {
    private E buffered;
    private Iterator<E> iterator;

    public BufferedIterator(Iterator<E> iterator, E buffered) {
        this.iterator = iterator;
        this.buffered = buffered;
    }

    public boolean hasNext() {
        return doNext() != null;
    }

    private E doNext() {
        if(buffered == null)
            buffered = iterator.next();
        return buffered;
    }

    public E next() {
        E next = doNext();
        buffered = null;
        return next;
    }

    public void remove() {
        iterator.remove();
    }
}
