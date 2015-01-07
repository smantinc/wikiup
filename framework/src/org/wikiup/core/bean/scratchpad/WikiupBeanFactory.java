package org.wikiup.core.bean.scratchpad;

import org.wikiup.core.Constants;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.impl.factory.FactoryByName;
import org.wikiup.core.impl.factory.FactoryByNamespace;
import org.wikiup.core.impl.factory.FactoryImpl;
import org.wikiup.core.impl.factory.ToWirableByDocument;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.ClassIdentity;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WikiupBeanFactory extends WikiupDynamicSingleton<WikiupBeanFactory> implements Context<Factory.ByName<?>, Factory.ByName<?>>, Iterable<String> {
    private FactoryImpl<Object, Object> root;
    private FactoryByNamespace<Object> factories;
    private Map<String, Factory.ByName<Object>> byNames;

    public void firstBuilt() {
        root = new FactoryImpl<Object, Object>();
        factories = new FactoryByNamespace<Object>();
        byNames = new HashMap<String, Factory.ByName<Object>>();
        factories.set(null, new DefaultBeanFactory());
    }

    public void addInterfaceAlias(Class<?> inf, String alias, Factory.ByName<?> factory) {
//        boolean contains = nameAlias.containsKey(inf);
//        Map<String, Factory.ByName<?>> a = contains ? nameAlias.get(inf) : new HashMap<String, Factory.ByName<?>>();
//        a.put(alias, factory);
//        if(!contains)
//            nameAlias.put(inf, a);
    }

//    public Factory.ByName<?> getModelFactory(Class<?> inf, ClassIdentity alias) {
//        Map<String, Factory.ByName<?>> a = nameAlias.get(inf);
//        return a != null ? a.get(alias.getNamespace()) : factories.get(alias.getNamespace());
//    }

    public Factory.ByName<?> get(String name) {
        return factories.get(name);
    }

    public void set(String name, Factory.ByName<?> factory) {
        appendModelFactoryByNames(factory);
        addFactory(name, factory);
    }

    private void addFactory(String name, Factory.ByName<?> factory) {
        factories.set(name, (Factory.ByName<Object>) factory);
    }

    public void loadBeans(Document desc) {
        for(Document doc : desc.getChildren()) {
            String name = Documents.getId(doc);
            Factory.ByName<?> f = factories.get(name);
            if(f == null)
                addFactory(name, (f = buildFactory(doc)));
            Interfaces.initialize(f, doc);
            appendModelFactoryByNames(f);
            set(name, f);
            addInterfaceAlias(doc, f);
        }
    }

    private Factory.ByName<?> buildFactory(Document desc) {
        ClassIdentity csid = ClassIdentity.obtain(desc);
        Factory.ByName<?> modelFactory = csid.getBean(Factory.ByName.class);
        return modelFactory != null ? modelFactory : new ToWirableByDocument<Object>(factories);
    }

    private void appendModelFactoryByNames(Factory.ByName<?> factory) {
        Iterable<String> iterable = Interfaces.foreach(factory);
        for(String name : iterable)
            addFactory(name, factory);
    }

    private void addInterfaceAlias(Document doc, Factory.ByName<?> factory) {
        String inf = Documents.getAttributeValue(doc, Constants.Attributes.INTERFACE, null);
        if(inf != null)
            addInterfaceAlias(Interfaces.getClass(inf), Documents.getAttributeValue(doc, Constants.Attributes.ALIAS, null), factory);
    }

    public Iterator<String> iterator() {
        return factories.getFactorys().keySet().iterator();
    }

    private class DefaultBeanFactory implements Factory.ByName<Object> {
        private FactoryByName<Object> classFactory = new FactoryByName<Object>();

        public Object build(String name) {
            Factory.ByName<Object> factory = byNames.get(name);
            Object mc = factory != null ? factory.build(name) : null;
            return mc != null ? mc : classFactory.build(name);
        }
    }
}
