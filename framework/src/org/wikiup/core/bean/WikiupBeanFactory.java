package org.wikiup.core.bean;

import java.util.Iterator;

import org.wikiup.core.Constants;
import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.attribute.AttributeImpl;
import org.wikiup.core.impl.document.DocumentWrapper;
import org.wikiup.core.impl.factory.BeanFactory;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.ClassIdentity;
import org.wikiup.core.util.Interfaces;

public class WikiupBeanFactory extends WikiupDynamicSingleton<WikiupBeanFactory> implements Context<Factory<?>, Factory<?>>, Iterable<Object> {
    private BeanFactory beanFactory;

    public void firstBuilt() {
        beanFactory = new BeanFactory(Constants.Factories.BY_CLASS_NAME);
        beanFactory.add(Object.class, new DefaultObjectClass(beanFactory));
    }

    @Override
    public Factory<?> get(String name) {
        return beanFactory.get(name);
    }

    @Override
    public void set(String name, Factory<?> factory) {
        addFactory(name, factory);
    }

    public <E> E build(Class<E> clazz, Document desc) {
        return beanFactory.build(clazz, desc);
    }

    public <T, P> T build(Class<T> clazz, Document desc, P wire) {
        return beanFactory.build(clazz, desc, wire);
    }

    @Deprecated
    public <E> E build(Class<E> clazz, String namespace, String name) {
        return Interfaces.cast(clazz, beanFactory.buildByNamespace(namespace, new IdentityDocument(Null.getInstance(), name)));
    }

    private void addFactory(String name, Factory<?> factory) {
        beanFactory.add(name, factory);
    }

    public Iterator<Object> iterator() {
        return beanFactory.getFactories().keySet().iterator();
    }

    public void loadBeans(Document desc, Factory<Factory<?>> builder) {
        beanFactory.loadFactories(desc, builder);
    }

    private static class DefaultObjectClass implements Factory<Object> {
        private BeanFactory beanFactory;

        public DefaultObjectClass(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public Object build(Document desc) {
            ClassIdentity classIdentity = ClassIdentity.obtain(desc);
            return beanFactory.buildByNamespace(classIdentity.getNamespace(), new IdentityDocument(desc, classIdentity.getName()));
        }
    }

    private static class IdentityDocument extends DocumentWrapper {
        private String className;

        public IdentityDocument(Document doc, String className) {
            super(doc);
            this.className = className;
        }

        @Override
        public Attribute getAttribute(String name) {
            if(Constants.Attributes.CLASS.equals(name))
                return new AttributeImpl(name, className);
            return super.getAttribute(name);
        }
    }
}
