package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.cl.ClassDictionaryImpl;
import org.wikiup.core.inf.Decorator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Interfaces;

public class FactoryWithWirable<T> implements Factory<T> {
    private final Factory<?> factory;

    public FactoryWithWirable() {
        this.factory = new FactoryImpl<Object>(new ClassDictionaryImpl());
    }

    public FactoryWithWirable(Factory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T build(Document desc) {
        Object bean = factory.build(desc);
        Wirable.ByDocument<?> wirable = Interfaces.cast(Wirable.ByDocument.class, bean);
        if(wirable != null)
            bean = wirable.wire(desc);
        return (T) bean;
    }

    public static final class DECORATOR<T> implements Decorator<Factory<T>> {
        @Override
        public Factory<T> decorate(Factory<T> factory, Document desc) {
            return new FactoryWithWirable<T>(factory);
        }
    }
}
