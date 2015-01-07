package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.cl.ClassDictionaryByName;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Wirable;
import org.wikiup.core.inf.ext.ClassDictionary;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;

public class FactoryByName<T> implements Factory.ByName<T> {
    private ClassDictionary classDictionary;

    public FactoryByName() {
    }

    public FactoryByName(ClassDictionary classDictionary) {
        this.classDictionary = classDictionary;
    }
    
    @Override
    public T build(String className) {
        Class<?> clazz = null;
        try {
            clazz = classDictionary != null ? classDictionary.get(className) : Class.forName(className);
        } catch(ClassNotFoundException e) {
            Assert.fail(e);
        }
        Assert.notNull(clazz, ClassNotFoundException.class, className);
        return className != null ? Interfaces.newInstance((Class<T>) clazz) : null;
    }
    
    public static final class WIRABLE<T> extends WrapperImpl<FactoryByName<T>> implements Wirable.ByDocument<FactoryByName<T>> {
        public WIRABLE(FactoryByName<T> wrapped) {
            super(wrapped);
        }

        @Override
        public FactoryByName<T> wire(Document desc) {
            wrapped.classDictionary = new ClassDictionaryByName(desc);
            return wrapped;
        }
    }
}
