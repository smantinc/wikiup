package org.wikiup.core.impl.getter;

import org.wikiup.core.impl.iterable.ReversedIterable;
import org.wikiup.core.inf.Getter;

import java.util.Iterator;
import java.util.Stack;

public class StackGetter<E> extends GetterCollection<E> {
    public StackGetter() {
        super(new StackCollection<Getter<E>>());
    }

    private static class StackCollection<E> extends Stack<E> {
        @Override
        public Iterator<E> iterator() {
            return new ReversedIterable<E>(this).iterator();
        }
    }
}
