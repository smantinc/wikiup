package org.wikiup.core.impl.factory;

import org.wikiup.core.Constants;
import org.wikiup.core.inf.Decorator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Wirable;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.ClassIdentity;
import org.wikiup.core.util.Documents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FactoryByNamespace<T> implements Factory.ByName<T>, Iterable<String>, Context<Factory.ByName<T>, Factory.ByName<T>> {
    private final Map<String, Factory.ByName<T>> factorys;

    public FactoryByNamespace() {
        factorys = new HashMap<String, ByName<T>>();
    }

    public FactoryByNamespace(Map<String, ByName<T>> factorys) {
        this.factorys = factorys;
    }

    public FactoryByNamespace(Document desc, Factory.ByDocument<Factory.ByName<T>> builder) {
        factorys = new HashMap<String, ByName<T>>();
        for(Document node : desc.getChildren()) {
            String namespace = Documents.getId(node);
            if(!factorys.containsKey(namespace) || Documents.getAttributeBooleanValue(desc, Constants.Attributes.OVERRIDE, false))
                add(namespace, builder.build(node));
        }
    }

    @Override
    public T build(String id) {
        ClassIdentity csid = ClassIdentity.obtain(id);
        Factory.ByName<T> factory = factorys.get(csid.getNamespace());
        return factory != null ? factory.build(csid.getName()) : null;
    }

    public void add(String namespace, Factory.ByName<T> factory) {
        factorys.put(namespace, factory);
    }

    @Override
    public Iterator<String> iterator() {
        return factorys.keySet().iterator();
    }

    @Override
    public ByName<T> get(String name) {
        return factorys.get(name);
    }

    @Override
    public void set(String name, ByName<T> obj) {
        factorys.put(name, obj);
    }

    public Map<String, ByName<T>> getFactorys() {
        return factorys;
    }

    public static final class BUILDER<T> implements Wirable.ByDocument<FactoryByNamespace<T>>, Decorator<FactoryByNamespace<T>> {
        private Factory.ByDocument<Factory.ByName<T>> builder;

        public BUILDER(Factory.ByDocument<Factory.ByName<T>> builder) {
            this.builder = builder;
        }

        @Override
        public FactoryByNamespace<T> wire(Document desc) {
            return new FactoryByNamespace<T>(desc, builder);
        }

        @Override
        public FactoryByNamespace<T> decorate(FactoryByNamespace<T> factory, Document desc) {
            Factory.ByName<T> f = builder.build(desc);
            String namespace = Documents.ensureAttributeValue(desc, Constants.Attributes.NAME);
            factory.add(namespace, f);
            return factory;
        }
    }
}
