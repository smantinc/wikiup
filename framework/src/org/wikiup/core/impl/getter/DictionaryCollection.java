package org.wikiup.core.impl.getter;

import org.wikiup.core.inf.Dictionary;

import java.util.Collection;

public class DictionaryCollection<E> implements Dictionary<E> {
    private Collection<Dictionary<E>> collection;
    private Iterable<Dictionary<E>> iterable;

    public DictionaryCollection(Iterable<? extends Dictionary<E>> collection) {
        this(collection, collection);
    }

    public DictionaryCollection(Iterable<? extends Dictionary<E>> collection, Iterable<? extends Dictionary<E>> iterable) {
        this.collection = (Collection<Dictionary<E>>) collection;
        this.iterable = (Iterable<Dictionary<E>>) iterable;
    }

    public DictionaryCollection<E> append(Dictionary<E>... dictionaries) {
        for(Dictionary<E> g : dictionaries)
            collection.add(g);
        return this;
    }

    public void remove(Dictionary<E>... dictionaries) {
        for(Dictionary<E> g : dictionaries)
            collection.remove(g);
    }

    public E get(String name) {
        for(Dictionary<E> g : iterable) {
            E obj = g.get(name);
            if(obj != null)
                return obj;
        }
        return null;
    }
}
