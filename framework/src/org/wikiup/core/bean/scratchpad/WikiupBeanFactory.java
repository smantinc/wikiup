package org.wikiup.core.bean.scratchpad;

import org.wikiup.core.Constants;
import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.impl.factory.FactoryByClass;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Wirable;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.ClassIdentity;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WikiupBeanFactory extends WikiupDynamicSingleton<WikiupBeanFactory> implements Context<Factory.ByName<?>, Factory.ByName<?>>, Iterable<Object> {
    private Map<Object, Factory.ByName<?>> factories;

    public void firstBuilt() {
        factories = new HashMap<Object, Factory.ByName<?>>();
    }

    public void addInterfaceAlias(Class<?> inf, String alias, Factory.ByName<?> factory) {
        factories.put(inf, factory);
        factories.put(alias, factory);
    }

    public Factory.ByName<?> get(String name) {
        return factories.get(name);
    }

    public void set(String name, Factory.ByName<?> factory) {
        addFactory(name, factory);
    }

    private void addFactory(String name, Factory.ByName<?> factory) {
        factories.put(name, factory);
    }

    private void addInterfaceAlias(Document doc, Factory.ByName<?> factory) {
        String inf = Documents.getAttributeValue(doc, Constants.Attributes.INTERFACE, null);
        if(inf != null)
            addInterfaceAlias(Interfaces.getClass(inf), Documents.getAttributeValue(doc, Constants.Attributes.ALIAS, null), factory);
    }

    public Iterator<Object> iterator() {
        return factories.keySet().iterator();
    }

    public void loadBeans(Document desc, Factory.ByDocument<Factory.ByName<?>> factory) {
        for(Document doc : desc.getChildren()) {
            String name = Documents.getId(doc);
            Factory.ByName<?> f = factories.get(name);
            if(f == null)
                addFactory(name, (f = factory.build(doc)));
            set(name, f);
            addInterfaceAlias(doc, f);
        }
    }

    private class DefaultBeanFactory implements Factory.ByName<Object> {
        private FactoryByClass<Object> classFactory = new FactoryByClass<Object>();

        public Object build(String name) {
            Factory.ByName<?> factory = factories.get(name);
            Object mc = factory != null ? factory.build(name) : null;
            return mc != null ? mc : classFactory.build(name);
        }
    }
}
