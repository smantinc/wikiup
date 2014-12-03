package org.wikiup.core.bean;

import org.wikiup.core.impl.cl.DefaultClassLoader;
import org.wikiup.core.impl.getter.GetterCollection;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.Setter;
import org.wikiup.core.inf.ext.ClassLoader;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WikiupClassLoader extends WikiupDynamicSingleton<WikiupClassLoader> implements ClassLoader, Setter<ClassLoader>, Releasable, Iterable<ClassLoader> {
    private List<ClassLoader> collection;
    private GetterCollection<Class> loaders;

    public Class get(String name) {
        Class clazz = loaders.get(name);
        Assert.notNull(clazz, ClassNotFoundException.class, name);
        return clazz;
    }

    public void firstBuilt() {
        collection = new ArrayList<ClassLoader>();
        collection.add(new DefaultClassLoader());
        loaders = new GetterCollection<Class>(collection);
    }

    public void set(String name, ClassLoader obj) {
        collection.add(0, obj);
    }

    public void release() {
        Interfaces.releaseAll(collection);
        collection.clear();
        collection = null;
    }

    public Iterator<ClassLoader> iterator() {
        return collection.iterator();
    }
}