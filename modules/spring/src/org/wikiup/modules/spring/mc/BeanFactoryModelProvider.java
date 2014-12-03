package org.wikiup.modules.spring.mc;

import org.springframework.beans.factory.BeanFactory;
import org.wikiup.core.inf.ModelProvider;

public class BeanFactoryModelProvider implements ModelProvider {
    private BeanFactory beanFactory;
    private String beanName;

    public BeanFactoryModelProvider(BeanFactory beanFactory, String beanName) {
        this.beanFactory = beanFactory;
        this.beanName = beanName;
    }

    public <E> E getModel(Class<E> clazz) {
        return (E) beanFactory.getBean(beanName, clazz);
    }
}
