package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.cl.ClassDictionaryByName;
import org.wikiup.core.impl.cl.ClassDictionaryImpl;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.ClassDictionary;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;

public class FactoryByClass<T> implements Factory.ByName<T> {
    private ClassDictionary classDictionary;

    private FactoryByClass() {
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
    
    public static final class WIRABLE<T> extends WrapperImpl<FactoryByClass<T>> implements Wirable.ByDocument<FactoryByClass<T>> {
        private ClassDictionaryByName classDictionaryByName = new ClassDictionaryByName();
        
        public WIRABLE() {
            super(new FactoryByClass<T>());
            wrapped.classDictionary = classDictionaryByName;
        }

        @Override
        public FactoryByClass<T> wire(Document desc) {
            classDictionaryByName.wire(desc, new ClassDictionaryImpl());
            return wrapped;
        }
    }
}
