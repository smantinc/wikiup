package org.wikiup.core.impl.factory;

import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;

public class ByClass<T> implements Factory.ByName<T> {
    private Getter<Class> classLoader;

    public ByClass() {
    }

    public ByClass(Getter<Class> classLoader) {
        this.classLoader = classLoader;
    }
    
    @Override
    public T build(String className) {
        Class<?> clazz = null;
        try {
            clazz = classLoader != null ? classLoader.get(className) : Class.forName(className);
        } catch(ClassNotFoundException e) {
            Assert.fail(e);
        }
        Assert.notNull(clazz, ClassNotFoundException.class, className);
        return className != null ? Interfaces.newInstance((Class<T>) clazz) : null;
    }
}
