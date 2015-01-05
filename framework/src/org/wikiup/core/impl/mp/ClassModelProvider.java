package org.wikiup.core.impl.mp;

import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.util.Interfaces;

public class ClassModelProvider implements BeanFactory {
    public <E> E query(Class<E> clazz) {
        return Interfaces.newInstance(clazz);
    }
}
