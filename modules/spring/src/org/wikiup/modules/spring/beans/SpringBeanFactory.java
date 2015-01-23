package org.wikiup.modules.spring.beans;

import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.modules.spring.WikiupSpringDynamicSingleton;

import java.util.Iterator;

public class SpringBeanFactory implements ModelFactory, Iterable<String>, Factory<BeanContainer, String> {
    private WikiupSpringDynamicSingleton spring = Wikiup.getModel(WikiupSpringDynamicSingleton.class);

    public BeanContainer get(String name) {
        return spring.get(name);
    }

    public Iterator<String> iterator() {
        return spring.iterator();
    }

    @Override
    public BeanContainer build(String name) {
        return spring.get(name);
    }
}
