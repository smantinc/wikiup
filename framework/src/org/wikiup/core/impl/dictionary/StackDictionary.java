package org.wikiup.core.impl.dictionary;

import org.wikiup.core.impl.iterable.ReversedIterable;
import org.wikiup.core.inf.Dictionary;

import java.util.Iterator;
import java.util.Stack;

public class StackDictionary<E> extends DictionaryCollection<E> {
    public StackDictionary() {
        super(new StackCollection<Dictionary<E>>());
    }

    private static class StackCollection<E> extends Stack<E> {
        @Override
        public Iterator<E> iterator() {
            return new ReversedIterable<E>(this).iterator();
        }
    }
}
