package org.wikiup.core.bean;

import org.wikiup.core.Constants;
import org.wikiup.core.impl.cl.ClassDictionaryImpl;
import org.wikiup.core.impl.factory.BeanFactory;
import org.wikiup.core.impl.factory.FactoryByName;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.ClassIdentity;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.util.Iterator;

public class WikiupBeanFactory extends WikiupDynamicSingleton<WikiupBeanFactory> implements Context<Factory<?, ?>, Factory<?, ?>>, Iterable<Object> {
    private BeanFactory beanFactory;

    public void firstBuilt() {
        beanFactory = new BeanFactory();
        beanFactory.add(Object.class, new DefaultObjectClass(beanFactory));
        beanFactory.add(null, Constants.Factories.BY_CLASS_NAME);
    }

    @Override
    public Factory<?, ?> get(String name) {
        return beanFactory.build(name);
    }

    @Override
    public void set(String name, Factory<?, ?> factory) {
        addFactory(name, factory);
    }

    public <E> E build(Class<E> clazz, Document desc) {
        return beanFactory.build(clazz, Documents.getAttributeValue(desc, Constants.Attributes.CLASS, null), desc);
    }

    public <E> E build(Class<E> clazz, String namespace, String name) {
        return Interfaces.cast(clazz, beanFactory.build(namespace, name));
    }

    public <E> E build(Class<E> clazz, String name, Document desc) {
        return beanFactory.build(clazz, name, desc);
    }
    
    private void addFactory(String name, Factory<?, ?> factory) {
        beanFactory.add(name, factory);
    }

    public Iterator<Object> iterator() {
        return beanFactory.getFactories().keySet().iterator();
    }

    public void loadBeans(Document desc, Factory.ByDocument<Factory<?, ?>> builder) {
        beanFactory.loadFactories(desc, builder);
    }

    private static class DefaultObjectClass implements Factory<Object, String> {
        private BeanFactory beanFactory;
        
        public DefaultObjectClass(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }
        
        @Override
        public Object build(String name) {
            ClassIdentity classIdentity = ClassIdentity.obtain(name);
            return beanFactory.build(classIdentity.getNamespace(), classIdentity.getName());
        }
    }
}
