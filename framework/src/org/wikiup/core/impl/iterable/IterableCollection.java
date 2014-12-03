package org.wikiup.core.impl.iterable;

import org.wikiup.core.impl.Null;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class IterableCollection<E> implements Iterable<E> {
    private List<Iterable<E>> iterables = new LinkedList<Iterable<E>>();

    public IterableCollection(List<Iterable<E>> iterables) {
        this.iterables = iterables;
    }

    public IterableCollection(Iterable<E>... args) {
        append(args);
    }

    public IterableCollection<E> append(Iterable<E>... args) {
        for(Iterable<E> iterable : args)
            iterables.add(iterable);
        return this;
    }

    public Iterator<E> iterator() {
        return new CompositeIterator<E>(iterables);
    }

    private static class CompositeIterator<E> implements Iterator<E> {
        private List<Iterable<E>> iterables = new LinkedList<Iterable<E>>();
        private int cursor = 0;
        private Iterator<E> iterator = Null.getInstance();

        public CompositeIterator(List<Iterable<E>> iterables) {
            for(Iterable<E> iterable : iterables)
                this.iterables.add(iterable);
        }

        public boolean hasNext() {
            seekCurrentIterator();
            return iterator.hasNext();
        }

        public E next() {
            seekCurrentIterator();
            return iterator.next();
        }

        private void seekCurrentIterator() {
            while(!iterator.hasNext() && cursor < iterables.size())
                iterator = iterables.get(cursor++).iterator();
        }

        public void remove() {
            iterator.remove();
        }
    }
}
