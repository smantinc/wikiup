package org.wikiup.core.impl.iterable;

import java.util.Iterator;
import java.util.List;

public class ReversedIterable<E> implements Iterable<E> {
    private List<E> iterable;

    public ReversedIterable(List<E> iterable) {
        this.iterable = iterable;
    }

    public Iterator<E> iterator() {
        return new ReversedIterator();
    }

    private class ReversedIterator implements Iterator<E> {
        private int index = iterable.size();

        public boolean hasNext() {
            return index > 0;
        }

        public E next() {
            return iterable.get(--index);
        }

        public void remove() {
            iterable.remove(--index);
        }
    }
}
