package org.wikiup.core.impl.factory;

import org.wikiup.core.Constants;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Wirable;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

public class FactoryByDocument<T> implements Factory.ByDocument<T> {
    private Factory.ByName<?> factory;

    public FactoryByDocument(Factory.ByName<?> factory) {
        this.factory = factory;
    }
    
    @Override
    public T build(Document desc) {
        String name = Documents.ensureAttributeValue(desc, Constants.Attributes.NAME);
        Object bean = factory.build(name);
        Wirable.ByDocument<?> wirable = Interfaces.cast(Wirable.ByDocument.class, bean);
        if(wirable != null)
            bean = wirable.wire(desc);
        return Interfaces.cast(bean);
    }
}
