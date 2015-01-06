package org.wikiup.core.bean;

import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.mf.ClassFactory;
import org.wikiup.core.impl.mf.ClassNameFactory;
import org.wikiup.core.impl.mf.NamespaceFactory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.core.util.ClassIdentity;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WikiupBeanFactory extends WikiupDynamicSingleton<WikiupBeanFactory> implements Context<ModelFactory, ModelFactory>, Iterable<String> {
    private Map<Class<?>, Map<String, ModelFactory>> nameAlias;
    private NamespaceFactory factories;
    private Map<String, ModelFactory> byNames;

    public void firstBuilt() {
        nameAlias = new HashMap<Class<?>, Map<String, ModelFactory>>();
        factories = new NamespaceFactory();
        byNames = new HashMap<String, ModelFactory>();
        factories.addFactory(null, new DefaultBeanFactory());
    }

    public void addInterfaceAlias(Class<?> inf, String alias, ModelFactory factory) {
        boolean contains = nameAlias.containsKey(inf);
        Map<String, ModelFactory> a = contains ? nameAlias.get(inf) : new HashMap<String, ModelFactory>();
        a.put(alias, factory);
        if(!contains)
            nameAlias.put(inf, a);
    }

    public ModelFactory getModelFactory(Class<?> inf, ClassIdentity alias) {
        Map<String, ModelFactory> a = nameAlias.get(inf);
        return a != null ? a.get(alias.getNamespace()) : factories.getFactory(alias.getNamespace());
    }

    public ModelFactory get(String name) {
        return factories.getFactory(name);
    }

    public void set(String name, ModelFactory factory) {
        appendModelFactoryByNames(factory);
        factories.addFactory(name, factory);
    }

    public void loadBeans(Document desc) {
        for(Document doc : desc.getChildren()) {
            String name = Documents.getId(doc);
            ModelFactory f = factories.getFactory(name);
            if(f == null)
                factories.addFactory(name, (f = buildFactory(doc)));
            Interfaces.initialize(f, doc);
            appendModelFactoryByNames(f);
            set(name, f);
            addInterfaceAlias(doc, f);
        }
    }

    private ModelFactory buildFactory(Document desc) {
        ClassIdentity csid = Wikiup.getClassIdentity(desc);
        ModelFactory modelFactory = csid.getBean(ModelFactory.class);
        return modelFactory != null ? modelFactory : new ClassNameFactory();
    }

    private void appendModelFactoryByNames(ModelFactory factory) {
        Iterable<String> iterable = Interfaces.foreach(factory);
        for(String name : iterable)
            byNames.put(name, factory);
    }

    private void addInterfaceAlias(Document doc, ModelFactory factory) {
        String inf = Documents.getAttributeValue(doc, "interface", null);
        if(inf != null)
            addInterfaceAlias(Interfaces.getClass(inf), Documents.getAttributeValue(doc, "alias", null), factory);
    }

    public Iterator<String> iterator() {
        return factories.getFactorys().keySet().iterator();
    }

    private class DefaultBeanFactory implements ModelFactory {
        private ClassFactory classFactory = new ClassFactory();

        public BeanContainer get(String name) {
            BeanContainer mc = find(name);
            return mc != null ? mc : classFactory.get(name);
        }

        private BeanContainer find(String name) {
            ModelFactory modelFactory = byNames.get(name);
            return modelFactory != null ? modelFactory.get(name) : null;
        }
    }
}