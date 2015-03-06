package org.wikiup.core.bean;

import org.wikiup.core.impl.cl.ClassDictionaryImpl;
import org.wikiup.core.impl.getter.DictionaryCollection;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.ClassDictionary;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WikiupClassLoader extends WikiupDynamicSingleton<WikiupClassLoader> implements ClassDictionary, Dictionary.Mutable<ClassDictionary>, Releasable, Iterable<ClassDictionary> {
    private List<ClassDictionary> collection;
    private DictionaryCollection<Class> loaders;

    public Class get(String name) {
        Class clazz = loaders.get(name);
        Assert.notNull(clazz, ClassNotFoundException.class, name);
        return clazz;
    }

    public void firstBuilt() {
        collection = new ArrayList<ClassDictionary>();
        collection.add(new ClassDictionaryImpl());
        loaders = new DictionaryCollection<Class>(collection);
    }

    public void set(String name, ClassDictionary obj) {
        collection.add(0, obj);
    }

    public void release() {
        Interfaces.releaseAll(collection);
        collection.clear();
        collection = null;
    }

    public Iterator<ClassDictionary> iterator() {
        return collection.iterator();
    }
}