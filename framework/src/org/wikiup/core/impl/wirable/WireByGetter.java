package org.wikiup.core.impl.wirable;

import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Wirable;

public class WireByGetter<T> extends WrapperImpl<T> implements Wirable<T, Getter<?>> {
    public WireByGetter(T wrapped) {
        super(wrapped);
    }

    @Override
    public T wire(Getter<?> getter) {
        BeanProperties properties = new BeanProperties(wrapped);
        for(BeanProperty property : properties) {
            if(property.isSettable())
                property.setObject(getter.get(property.getName()));
        }
        return wrapped;
    }
}
