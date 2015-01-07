package org.wikiup.core.impl.factory;

import org.wikiup.core.inf.Factory;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.Map;

public class FactoryImpl<T, P> implements Factory<T, P> {
    private Map<Object, T> beans = new HashMap<Object, T>();
    
    @Override
    public T build(P param) {
        return beans.get(param);
    }
    
    public ByName<T> asByName() {
        return Interfaces.cast(this);
    }

    public ByClass<T> asByClass() {
        return Interfaces.cast(this);
    }
}
