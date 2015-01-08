package org.wikiup.core.impl.factory;

import org.wikiup.core.Constants;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Wirable;
import org.wikiup.core.util.ClassIdentity;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.Map;

public class BeanFactory implements Factory<Factory<Object, Object>, Object> {
    private Map<Object, FactoryWrapper<Object, Object>> factories = new HashMap<Object, FactoryWrapper<Object, Object>>();

    @Override
    public FactoryWrapper<Object, Object> build(Object params) {
        return factories.get(params);
    }

    public <T> T build(Class<T> clazz, String id) {
        ClassIdentity csid = ClassIdentity.obtain(id);
        FactoryWrapper<Object, Object> factoryWrapper = getFactory(clazz, csid.getNamespace());
        Object bean = factoryWrapper != null ? factoryWrapper.asByName().build(csid.getName()) : null;
        return Interfaces.cast(clazz, bean);
    }

    public <T> T build(Class<T> clazz, Document document) {
        String id = Documents.getAttributeValue(document, Constants.Attributes.CLASS, null);
        return id != null ? build(clazz, id, document) : null;
    }
    
    public <T> T build(Class<T> clazz, String id, Document document) {
        ClassIdentity csid = ClassIdentity.obtain(id);
        FactoryWrapper<Object, Object> factoryWrapper = getFactory(clazz, csid.getNamespace());
        Object bean = factoryWrapper != null ? factoryWrapper.asByName().build(csid.getName()) : null;
        Wirable.ByDocument<T> wirable = Interfaces.cast(Wirable.ByDocument.class, bean);
        return wirable != null ? wirable.wire(document) : Interfaces.cast(clazz, bean);
    }
    
    private FactoryWrapper<Object, Object> getFactory(Class<?> clazz, String namespace) {
        FactoryWrapper<Object, Object> factoryWrapper = factories.get(clazz);
        return factoryWrapper != null ? factoryWrapper : factories.get(namespace);
    }
}
