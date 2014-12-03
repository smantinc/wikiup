package org.wikiup.core.impl.getter;

import org.wikiup.core.inf.Getter;

import java.util.Collection;

public class GetterCollection<E> implements Getter<E> {
    private Collection<Getter<E>> collection;
    private Iterable<Getter<E>> iterable;

    public GetterCollection(Iterable<? extends Getter<E>> collection) {
        this(collection, collection);
    }

    public GetterCollection(Iterable<? extends Getter<E>> collection, Iterable<? extends Getter<E>> iterable) {
        this.collection = (Collection<Getter<E>>) collection;
        this.iterable = (Iterable<Getter<E>>) iterable;
    }

    public GetterCollection<E> append(Getter<E>... getters) {
        for(Getter<E> g : getters)
            collection.add(g);
        return this;
    }

    public void remove(Getter<E>... getters) {
        for(Getter<E> g : getters)
            collection.remove(g);
    }

    public E get(String name) {
        for(Getter<E> g : iterable) {
            E obj = g.get(name);
            if(obj != null)
                return obj;
        }
        return null;
    }
}
