package org.wikiup.core.impl.factory;

import org.wikiup.core.Constants;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

public class FactoryByDocument<T> implements Factory.ByDocument<T> {
    private final Factory.ByName<?> factory;

    public FactoryByDocument() {
        this.factory = new FactoryByClass<Object>();
    }

    public FactoryByDocument(Factory.ByName<?> factory) {
        this.factory = factory;
    }
    
    @Override
    public T build(Document desc) {
        String name = Documents.getAttributeValue(desc, Constants.Attributes.CLASS, null);
        if(name == null)
            return null;
        Object bean = factory.build(name);
        Wirable.ByDocument<?> wirable = Interfaces.cast(Wirable.ByDocument.class, bean);
        if(wirable != null)
            bean = wirable.wire(desc);
        return (T) bean;
    }
}
