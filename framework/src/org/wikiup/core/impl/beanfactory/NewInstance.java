package org.wikiup.core.impl.beanfactory;

import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.util.Interfaces;

public class NewInstance implements BeanFactory {
    public <E> E query(Class<E> clazz) {
        return Interfaces.newInstance(clazz);
    }
}
