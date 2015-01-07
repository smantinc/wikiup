package org.wikiup.core.inf;

public interface Factory<T, P> {
    public T build(P params);
    
    public interface ByName<T> extends Factory<T, String> {
    }
    
    public interface ByClass<T> extends Factory<T, Class<?>> {
    }

    public interface ByDocument<T> extends Factory<T, Document> {
    }

    public interface ToWirable<T, E, P> extends Factory<Wirable<T, E>, P> {
    }

    public interface ByNameToWirable<T, E> extends ByName<Wirable<T, E>> {
    }
    
    public interface ByClassToWirable<T, E> extends ByClass<Wirable<T, E>> {
    }

    public interface ByDocumentToWirable<T, E> extends ByDocument<Wirable<T, E>> {
    }
}
