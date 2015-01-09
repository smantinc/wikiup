package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.cl.ClassDictionaryByName;
import org.wikiup.core.impl.cl.ClassDictionaryImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Wirable;
import org.wikiup.core.inf.ext.ClassDictionary;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;

public class FactoryByClass<T> implements Factory.ByName<T>, Wirable.ByDocument<FactoryByClass<T>> {
    private ClassDictionary classDictionary;

    public FactoryByClass() {
        classDictionary = new ClassDictionaryImpl();
    }

    public FactoryByClass(ClassDictionary classDictionary) {
        this.classDictionary = classDictionary;
    }

    public FactoryByClass(Document desc) {
        this.classDictionary = new ClassDictionaryByName(desc);
    }
    
    @Override
    public T build(String className) {
        Class<?> clazz = classDictionary.get(className);
        Assert.notNull(clazz, ClassNotFoundException.class, className);
        return className != null ? Interfaces.newInstance((Class<T>) clazz) : null;
    }

    @Override
    public FactoryByClass<T> wire(Document desc) {
        classDictionary = new ClassDictionaryByName(desc);
        return this;
    }
}
