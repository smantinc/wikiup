package org.wikiup.core.impl.factory;

import org.wikiup.core.Constants;
import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.Map;

public class BeanFactory implements Factory<Factory<Object, Object>, String> {
    private Map<Object, FactoryWrapper<Object, Object>> factories = new HashMap<Object, FactoryWrapper<Object, Object>>();
    private FactoryWrapper<Object, Object> defaultFactory;

    public BeanFactory() {
    }

    public BeanFactory(Factory<Object, ?> defaultFactory) {
        this.defaultFactory = new FactoryWrapper<Object, Object>((Factory) defaultFactory);
    }

    @Override
    public Factory<Object, Object> build(String name) {
        return ensureFactory(name);
    }

    public Map<Object, FactoryWrapper<Object, Object>> getFactories() {
        return factories;
    }

    public <T> T build(Class<T> clazz, Document document) {
        String name = Documents.getAttributeValue(document, Constants.Attributes.CLASS, null);
        return name != null ? build(clazz, name, document) : null;
    }

    public <T> T build(Class<T> clazz, String name, Document document) {
        Object bean = build(clazz, name);
        Wirable.ByDocument<T> wirable = Interfaces.cast(Wirable.ByDocument.class, bean);
        return wirable != null ? wirable.wire(document) : Interfaces.cast(clazz, bean);
    }

    public Object build(Object namespace, String name) {
        FactoryWrapper<Object, Object> factoryWrapper = factories.get(namespace);
        Object obj = factoryWrapper != null ? factoryWrapper.asByName().build(name) : null;
        return obj == null ? defaultFactory.asByName().build(name) : obj;
    }

    public void loadFactories(Document desc, Factory.ByDocument<Factory<?, ?>> builder) {
        for(Document doc : desc.getChildren()) {
            String name = Documents.getId(doc);
            FactoryWrapper<Object, Object> f = ensureFactory(name);
            String inf = Documents.getAttributeValue(doc, Constants.Attributes.INTERFACE, null);
            if(inf != null)
                factories.put(Interfaces.getClass(inf), f);
            Factory<Object, Object> factory = Interfaces.cast(builder.build(doc));
            f.wrap(factory);
        }
    }

    private FactoryWrapper<Object, Object> ensureFactory(Object name) {
        FactoryWrapper<Object, Object> f = factories.get(name);
        if(f == null)
            factories.put(name, f = new FactoryWrapper<Object, Object>(Null.getInstance()));
        return f;
    }

    public void add(Object name, Factory<?, ?> factory) {
        ensureFactory(name).wrap((Factory) factory);
    }
}
