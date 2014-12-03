package org.wikiup.core.inf;

public interface ModelProvider {
    public <E> E getModel(Class<E> clazz);
}
