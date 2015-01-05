package org.wikiup.core.impl.wirable;

import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.inf.Wirable;

public class WireByFields<T> extends WrapperImpl<T> implements Wirable<T, BeanFactory> {
    public WireByFields(T instance) {
        super(instance);
    }

    @Override
    public T wire(BeanFactory beanFactory) {
        BeanProperties properties = new BeanProperties(wrapped);
        for(BeanProperty property : properties) {
            if(property.isSettable())
                property.setObject(beanFactory.query(property.getPropertyClass()));
        }
        return wrapped;
    }
}
