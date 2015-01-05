package org.wikiup.core.inf;

@Deprecated
public interface ModelProvider {
    public <E> E getModel(Class<E> clazz);
}
