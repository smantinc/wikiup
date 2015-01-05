package org.wikiup.core.impl.mp;

import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SingletonModelProvider implements BeanFactory, Releasable {
    private Map<Class<?>, Object> byClasses = new HashMap<Class<?>, Object>();
    private BeanFactory factory = new ClassModelProvider();
    private Set<Object> singletons = new HashSet<Object>();

    public SingletonModelProvider() {
    }

    public SingletonModelProvider(BeanFactory factory) {
        this.factory = factory;
    }

    synchronized public <E> E query(Class<E> clazz) {
        E model = Interfaces.cast(clazz, byClasses.get(clazz));
        if(model == null)
            put(model = factory.query(clazz));
        return model;
    }

    public Object put(Object singleton) {
        Class<?> clazz = singleton.getClass();
        Object oldObject = byClasses.get(clazz);
        singletons.add(singleton);
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

    public void setByClasses(Map<Class<?>, Object> byClasses) {
        this.byClasses = byClasses;
    }

    public Map<Class<?>, Object> getByClasses() {
        return byClasses;
    }

    public void setSingletons(Set<Object> singletons) {
        this.singletons = singletons;
    }

    public Set<Object> getSingletons() {
        return singletons;
    }

    public void release() {
        Set<Object> localSet = new HashSet<Object>(singletons);
        byClasses.clear();
        singletons.clear();
        Interfaces.releaseAll(localSet);
    }
}
