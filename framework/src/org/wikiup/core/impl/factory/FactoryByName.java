package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.cl.ClassDictionaryByName;
import org.wikiup.core.impl.cl.ClassDictionaryImpl;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.ClassDictionary;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Interfaces;

public class FactoryByName<T> implements Factory.ByName<T> {
    private ClassDictionary classDictionary;

    private FactoryByName() {
    }

    public FactoryByName(ClassDictionary classDictionary) {
        this.classDictionary = classDictionary;
    }

    public FactoryByName(Document desc) {
        this.classDictionary = new ClassDictionaryByName(desc);
    }
    
    @Override
    public T build(String className) {
        Class<?> clazz = classDictionary.get(className);
        return clazz != null ? Interfaces.newInstance((Class<T>) clazz) : null;
    }
    
    public static final class WIRABLE<T> extends WrapperImpl<FactoryByName<T>> implements Wirable.ByDocument<FactoryByName<T>> {
        private ClassDictionaryByName classDictionaryByName = new ClassDictionaryByName();
        
        public WIRABLE() {
            super(new FactoryByName<T>());
            wrapped.classDictionary = classDictionaryByName;
        }

        @Override
        public FactoryByName<T> wire(Document desc) {
            classDictionaryByName.wire(desc, new ClassDictionaryImpl());
            return wrapped;
        }
    }
}
