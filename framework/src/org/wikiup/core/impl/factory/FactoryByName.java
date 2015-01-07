package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.cl.ClassDictionaryByName;
import org.wikiup.core.impl.cl.ClassDictionaryImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Wirable;
import org.wikiup.core.inf.ext.ClassDictionary;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;

public class FactoryByName<T> implements Factory.ByName<T> {
    private final ClassDictionary classDictionary;

    public FactoryByName() {
        classDictionary = new ClassDictionaryImpl();
    }

    public FactoryByName(ClassDictionary classDictionary) {
        this.classDictionary = classDictionary;
    }

    @Override
    public T build(String className) {
        Class<?> clazz = classDictionary.get(className);
        Assert.notNull(clazz, ClassNotFoundException.class, className);
        return className != null ? Interfaces.newInstance((Class<T>) clazz) : null;
    }

    public static final class WIRABLE<T> implements Wirable.ByDocument<FactoryByName<T>> {
        @Override
        public FactoryByName<T> wire(Document desc) {
            return new FactoryByName<T>(new ClassDictionaryByName(desc));
        }
    }
}
