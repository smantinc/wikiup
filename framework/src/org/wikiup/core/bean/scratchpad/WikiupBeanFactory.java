package org.wikiup.core.bean.scratchpad;

import java.util.Iterator;

import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.impl.factory.BeanFactory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.Context;

public class WikiupBeanFactory extends WikiupDynamicSingleton<WikiupBeanFactory> implements Context<Factory<?, ?>, Factory<?, ?>>, Iterable<Object> {
    private BeanFactory beanFactory;

    public void firstBuilt() {
        beanFactory = new BeanFactory();
    }

    @Override
    public Factory<?, ?> get(String name) {
        return beanFactory.build(name);
    }

    @Override
    public void set(String name, Factory<?, ?> factory) {
        addFactory(name, factory);
    }

    private void addFactory(String name, Factory<?, ?> factory) {
        beanFactory.add(name, factory);
    }

    public Iterator<Object> iterator() {
        return beanFactory.getFactories().keySet().iterator();
    }

    public void loadBeans(Document desc, Factory.ByDocument<Factory<?, ?>> factory) {
        beanFactory.loadFactories(desc, factory);
    }
}
