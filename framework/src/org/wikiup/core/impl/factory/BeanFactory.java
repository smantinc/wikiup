package org.wikiup.core.impl.factory;

import java.util.HashMap;
import java.util.Map;

import org.wikiup.core.Constants;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

public class BeanFactory implements Factory<Factory<Object, Object>, String> {
    private Map<Object, Factory<Object, Object>> factories = new HashMap<Object, Factory<Object, Object>>();
    private FactoryImpl defaultFactory;

    public BeanFactory(FactoryImpl defaultFactory) {
        this.defaultFactory = defaultFactory;
    }

    @Override
    public Factory<Object, Object> build(String name) {
        return factories.get(name);
    }

    public Map<Object, Factory<Object, Object>> getFactories() {
        return factories;
    }

    public <T> T build(Class<T> clazz, Document document) {
        String name = Documents.getAttributeValue(document, Constants.Attributes.CLASS, null);
        return name != null ? build(clazz, name, document) : null;
    }

    public <T> T build(Class<T> clazz, String name, Document document) {
        Object bean = buildByNamespace(clazz, document);
        Wirable.ByDocument<T> wirable = Interfaces.cast(Wirable.ByDocument.class, bean);
        return wirable != null ? wirable.wire(document) : Interfaces.cast(clazz, bean);
    }

    public Object buildByNamespace(Object namespace, Document name) {
        Factory<Object, Object> factory = factories.get(namespace);
        return factory != null ? factory.build(name) : defaultFactory.build(name);
    }

    public void loadFactories(Document desc, Factory.ByDocument<Factory<?, ?>> builder) {
        for(Document doc : desc.getChildren()) {
            String name = Documents.getId(doc);
            Factory<Object, Object> factory = Interfaces.cast(builder.build(doc));
            String inf = Documents.getAttributeValue(doc, Constants.Attributes.INTERFACE, null);
            if(inf != null)
                factories.put(Interfaces.getClass(inf), factory);
            factories.put(name, factory);
        }
    }

    public void add(Object name, Factory<?, ?> factory) {
        factories.put(name, (Factory) factory);
    }
}
