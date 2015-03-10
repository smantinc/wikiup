package org.wikiup.core.impl.factory;

import java.util.HashMap;
import java.util.Map;

import org.wikiup.core.Constants;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

public class BeanFactory implements Dictionary<Factory<?>> {
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
        return Interfaces.wire(clazz, bean, document);
    }

    public <T, P> T build(Class<T> clazz, Document document, P wire) {
        Object bean = buildByNamespace(clazz, document);
        return Interfaces.wire(clazz, bean, wire);
    }
    
    public Object buildByNamespace(Object namespace, Document doc) {
        Factory<?> factory = factories.get(namespace);
        return factory != null ? factory.build(doc) : defaultFactory.build(doc);
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
