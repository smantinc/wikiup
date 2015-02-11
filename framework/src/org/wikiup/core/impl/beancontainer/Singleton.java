package org.wikiup.core.impl.beancontainer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.util.Interfaces;

public class Singleton extends WrapperImpl<BeanContainer> implements BeanContainer, Releasable {
    private Map<Class<?>, Object> byClasses = new HashMap<Class<?>, Object>();

    public Singleton() {
        super(new NewInstance());
    }

    public Singleton(BeanContainer factory) {
        super(factory);
    }

    public synchronized <E> E query(Class<E> clazz) {
        E model = Interfaces.cast(clazz, byClasses.get(clazz));
        if(model == null)
            put(model = wrapped.query(clazz));
        return model;
    }

    public Object put(Object singleton) {
        Class<?> clazz = singleton.getClass();
        Object oldObject = byClasses.get(clazz);
        setModel(clazz, singleton);
        return oldObject;
    }

    private void setModel(Class<?> clazz, Object singleton) {
        while(!clazz.equals(Object.class)) {
            putInterfaces(clazz, singleton);
            byClasses.put(clazz, singleton);
            clazz = clazz.getSuperclass();
        }
    }

    private void putInterfaces(Class<?> clazz, Object singleton) {
        for(Class<?> inf : clazz.getInterfaces()) {
            byClasses.put(inf, singleton);
            putInterfaces(inf, singleton);
        }
    }

    public void release() {
        Set<Object> localSet = new HashSet<Object>(byClasses.values());
        byClasses.clear();
        Interfaces.releaseAll(localSet);
    }
}
