package org.wikiup.core.impl.iterable;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReversedIterable<T> implements Iterable<T> {
    private List<T> list;

    public ReversedIterable(List<T> list) {
        this.list = list;
    }

    public Iterator<T> iterator() {
        return new ReversedIterator<T>(list.listIterator(list.size()));
    }

    private static class ReversedIterator<T> implements Iterator<T> {
        private ListIterator<T> listIterator;

        public ReversedIterator(ListIterator<T> listIterator) {
            this.listIterator = listIterator;
        }
        
        public boolean hasNext() {
            return listIterator.hasPrevious();
        }

        public T next() {
            return listIterator.previous();
        }

        public void remove() {
            listIterator.remove();
        }
    }
}
