package org.wikiup.modules.spring.beans;

import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.modules.spring.WikiupSpringDynamicSingleton;

public class SpringModelProvider implements BeanFactory {
    private WikiupSpringDynamicSingleton spring = Wikiup.getModel(WikiupSpringDynamicSingleton.class);

    public <E> E query(Class<E> clazz) {
        return spring.query(clazz);
    }
}
