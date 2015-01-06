package org.wikiup.core.inf;

public interface Wirable<T, P> {
    public T wire(P params);
    
    public interface ByDocument<T> extends Wirable<T, Document> {
    }
}
