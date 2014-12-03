package org.wikiup.core.impl.getter;

import org.wikiup.core.inf.Getter;

public class GetterWrapper<E> implements Getter<E> {
    private Getter<E> getter;

    public GetterWrapper() {
    }

    public GetterWrapper(Getter<E> getter) {
        setGetter(getter);
    }

    public E get(String name) {
        return getter.get(name);
    }

    public void setGetter(Getter<E> getter) {
        this.getter = getter;
    }
}
