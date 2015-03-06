package org.wikiup.core.inf;

public interface Dictionary<T> {
    public T get(String name);

    interface Mutable<T> {
        public void set(String name, T obj);
    }
}
