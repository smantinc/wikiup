package org.wikiup.modules.spring.beans;

import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.modules.spring.WikiupSpringDynamicSingleton;

import java.util.Iterator;

public class SpringBeanFactory implements ModelFactory, Iterable<String> {
    private WikiupSpringDynamicSingleton spring = Wikiup.getModel(WikiupSpringDynamicSingleton.class);

    public BeanFactory get(String name) {
        return spring.get(name);
    }

    public Iterator<String> iterator() {
        return spring.iterator();
    }
}
