package org.wikiup.core.impl.iterable;

import java.util.Iterator;

public class GenericCastIterable<E> implements Iterable<E> {
    private Iterable<?> iterable;

    public GenericCastIterable(Iterable<?> iterable) {
        this.iterable = iterable;
    }

    public Iterator<E> iterator() {
        return new GenericCastIterator<E>(iterable.iterator());
    }

    private static class GenericCastIterator<E> implements Iterator<E> {
        private Iterator<?> iterator;

        public GenericCastIterator(Iterator<?> iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public E next() {
            return (E) iterator.next();
        }

        public void remove() {
            iterator.remove();
        }
    }
}
