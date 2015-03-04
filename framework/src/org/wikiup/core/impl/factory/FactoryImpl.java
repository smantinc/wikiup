package org.wikiup.core.impl.factory;

import org.wikiup.core.Constants;
import org.wikiup.core.impl.cl.ClassDictionaryByName;
import org.wikiup.core.impl.cl.ClassDictionaryImpl;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.ClassDictionary;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

public class FactoryImpl<T> implements Factory.ByDocument<T> {
    private ClassDictionary classDictionary;

    public FactoryImpl(ClassDictionary classDictionary) {
        this.classDictionary = classDictionary;
    }

    public FactoryImpl(Document desc) {
        this.classDictionary = new ClassDictionaryByName(desc);
    }

    @Override
    public T build(Document desc) {
        return build(Documents.ensureAttributeValue(desc, Constants.Attributes.CLASS));
    }
    
    public T build(String className) {
        Class<T> clazz = ((Class<T>) classDictionary.get(className));
        return clazz != null ? Interfaces.newInstance(clazz) : null;
    }

    public static final class WIRABLE<T> extends WrapperImpl<FactoryImpl<T>> implements Wirable.ByDocument<FactoryImpl<T>> {
        private ClassDictionaryByName classDictionary = new ClassDictionaryByName();

        public WIRABLE() {
            super(null);
            wrapped = new FactoryImpl<T>(classDictionary);
        }

        @Override
        public FactoryImpl<T> wire(Document desc) {
            classDictionary.wire(desc, new ClassDictionaryImpl());
            return wrapped;
        }
    }
}
