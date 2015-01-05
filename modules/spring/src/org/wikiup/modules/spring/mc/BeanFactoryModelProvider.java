package org.wikiup.modules.spring.mc;

import org.wikiup.core.inf.BeanFactory;

public class BeanFactoryModelProvider implements BeanFactory {
    private org.springframework.beans.factory.BeanFactory beanFactory;
    private String beanName;

    public BeanFactoryModelProvider(org.springframework.beans.factory.BeanFactory beanFactory, String beanName) {
        this.beanFactory = beanFactory;
        this.beanName = beanName;
    }

    public <E> E query(Class<E> clazz) {
        return (E) beanFactory.getBean(beanName, clazz);
    }
}
