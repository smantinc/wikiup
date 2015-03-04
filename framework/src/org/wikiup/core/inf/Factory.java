package org.wikiup.core.inf;

public interface Factory<T, P> {
    public T build(P params);
    
    public interface ByName<T> extends Factory<T, String> {
    }

    public interface ByDocument<T> extends Factory<T, Document> {
    }
}
