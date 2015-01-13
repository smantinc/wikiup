package org.wikiup.core.impl.wirable;

import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.ext.Wirable;

public class WireByFields<T> extends WrapperImpl<T> implements Wirable<T, BeanContainer> {
    public WireByFields(T instance) {
        super(instance);
    }

    @Override
    public T wire(BeanContainer beanContainer) {
        BeanProperties properties = new BeanProperties(wrapped);
        for(BeanProperty property : properties) {
            if(property.isSettable())
                property.setObject(beanContainer.query(property.getPropertyClass()));
        }
        return wrapped;
    }
}
