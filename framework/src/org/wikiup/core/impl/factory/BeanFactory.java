package org.wikiup.core.impl.factory;

import java.util.HashMap;
import java.util.Map;

import org.wikiup.core.Constants;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

public class BeanFactory implements Getter<Factory<?>> {
    private Map<Object, Factory<?>> factories = new HashMap<Object, Factory<?>>();
    private FactoryImpl defaultFactory;

    public BeanFactory(FactoryImpl defaultFactory) {
        this.defaultFactory = defaultFactory;
    }

    @Override
    public Factory<?> get(String name) {
        return factories.get(name);
    }

    public Map<Object, Factory<?>> getFactories() {
        return factories;
    }

    public <T> T build(Class<T> clazz, Document document) {
        Object bean = buildByNamespace(clazz, document);
        Wirable.ByDocument<T> wirable = Interfaces.cast(Wirable.ByDocument.class, bean);
        return wirable != null ? wirable.wire(document) : Interfaces.cast(clazz, bean);
    }

    public Object buildByNamespace(Object namespace, Document name) {
        Factory<?> factory = factories.get(namespace);
        return factory != null ? factory.build(name) : defaultFactory.build(name);
    }

    public void loadFactories(Document desc, Factory<Factory<?>> builder) {
        for(Document doc : desc.getChildren()) {
            String name = Documents.getId(doc);
            Factory<Object> factory = Interfaces.cast(builder.build(doc));
            String inf = Documents.getAttributeValue(doc, Constants.Attributes.INTERFACE, null);
            if(inf != null)
                factories.put(Interfaces.getClass(inf), factory);
            factories.put(name, factory);
        }
    }

    public void add(Object name, Factory<?> factory) {
        factories.put(name, factory);
    }
}
