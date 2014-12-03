package org.wikiup.modules.worms.imp.relatives;

import org.wikiup.core.inf.Attribute;
import org.wikiup.modules.worms.imp.component.Property;

import java.util.Iterator;

public class NonePropertyAttributeIterable<E extends Attribute> implements Iterable<E> {
    private Iterable<? extends E> iterable;

    public NonePropertyAttributeIterable(Iterable<? extends E> iterable) {
        this.iterable = iterable;
    }

    public Iterator<E> iterator() {
        return new NonePropertyAttributeIterator(iterable.iterator());
    }

    private static class NonePropertyAttributeIterator<E> implements Iterator<E> {
        private Iterator<E> iterator;
        private E next;

        public NonePropertyAttributeIterator(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return doNext() != null;
        }

        public E next() {
            E r = doNext();
            next = null;
            return r;
        }

        private E doNext() {
            if(next == null)
                do {
                    next = iterator.hasNext() ? iterator.next() : null;
                } while(next != null && next.getClass().equals(Property.class));
            return next;
        }

        public void remove() {
            iterator.remove();
        }
    }
}
