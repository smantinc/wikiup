package org.wikiup.core.impl.beancontainer;

import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Interfaces;

public class ByField extends WrapperImpl<Object> implements BeanContainer {
    public ByField(Object instance) {
        super(instance);
    }

    public <E> E query(Class<E> clazz) {
        Iterable<BeanProperty> iterable = new BeanProperties(wrapped);
        for(BeanProperty property : iterable)
            if(clazz.isAssignableFrom(property.getPropertyClass()) && property.isGettable())
                return Interfaces.cast(clazz, property.getObject());
        return null;
    }
}
