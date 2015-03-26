package org.wikiup.modules.spring.beans;

import org.wikiup.Wikiup;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.modules.spring.WikiupSpringDynamicSingleton;

public class SpringModelProvider implements BeanContainer {
    private WikiupSpringDynamicSingleton spring = Wikiup.getModel(WikiupSpringDynamicSingleton.class);

    public <E> E query(Class<E> clazz) {
        return spring.query(clazz);
    }
}
