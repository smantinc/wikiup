package org.wikiup.core.impl.beancontainer;

import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Interfaces;

public class NewInstance implements BeanContainer {
    public <T> T query(Class<T> clazz) {
        return clazz.isInterface() ? null : Interfaces.newInstance(clazz);
    }
}
