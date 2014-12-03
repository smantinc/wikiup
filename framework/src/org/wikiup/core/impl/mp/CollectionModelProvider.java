package org.wikiup.core.impl.mp;

import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.util.Interfaces;

import java.util.Collection;
import java.util.LinkedList;

public class CollectionModelProvider implements ModelProvider {
    private Collection<Object> containers = new LinkedList<Object>();

    public <E> E getModel(Class<E> clazz) {
        for(Object obj : containers) {
            E model = Interfaces.cast(clazz, obj);
            if(model != null)
                return model;
        }
        return null;
    }

    public void append(Object... args) {
        for(Object arg : args)
            containers.add(arg);
    }
}
