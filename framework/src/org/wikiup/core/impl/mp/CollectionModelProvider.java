package org.wikiup.core.impl.mp;

import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.util.Interfaces;

import java.util.Collection;
import java.util.LinkedList;

public class CollectionModelProvider implements BeanFactory {
    private Collection<Object> containers = new LinkedList<Object>();

    public <E> E query(Class<E> clazz) {
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
