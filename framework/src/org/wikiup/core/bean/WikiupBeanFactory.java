package org.wikiup.core.bean;

import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WikiupBeanFactory extends WikiupDynamicSingleton<WikiupBeanFactory> implements BeanFactory, Context<BeanFactory, BeanFactory>, Iterable<String>, Releasable {
    private Map<String, BeanFactory> beanFactories;
    private WikiupDynamicSingletons singletons;

    public void firstBuilt() {
        beanFactories = new HashMap<String, BeanFactory>();
        singletons = new WikiupDynamicSingletons();
    }

    public <E> E query(Class<E> clazz) {
        E e;
        for(BeanFactory mc : beanFactories.values())
            if((e = mc.query(clazz)) != null)
                return e;
        return singletons != null ? singletons.query(clazz) : null;
    }

    public BeanFactory get(String name) {
        return beanFactories.get(name);
    }

    public void set(String name, BeanFactory obj) {
        beanFactories.put(name, obj);
        if(singletons != null && singletons.getClass().equals(obj.getClass()))
            singletons = null;
    }

    public Iterator<String> iterator() {
        return beanFactories.keySet().iterator();
    }

    public void release() {
        Interfaces.release(singletons);
        Interfaces.releaseAll(beanFactories.values());
    }
}
