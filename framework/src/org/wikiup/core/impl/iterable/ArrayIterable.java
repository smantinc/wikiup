package org.wikiup.core.impl.iterable;

import java.util.Iterator;

public class ArrayIterable<E> implements Iterable<E> {
    private E[] array;

    public ArrayIterable(E[] array) {
        this.array = array;
    }

    public Iterator<E> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<E> {
        private int index = 0;

        public boolean hasNext() {
            return index < array.length;
        }

        public E next() {
            return array[index++];
        }

        public void remove() {
        }
    }
}
