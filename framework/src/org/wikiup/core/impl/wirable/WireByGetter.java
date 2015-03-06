package org.wikiup.core.impl.wirable;

import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.ext.Wirable;

public class WireByGetter<T> extends WrapperImpl<T> implements Wirable<T, Dictionary<?>> {
    public WireByGetter(T wrapped) {
        super(wrapped);
    }

    @Override
    public T wire(Dictionary<?> dictionary) {
        BeanProperties properties = new BeanProperties(wrapped);
        for(BeanProperty property : properties) {
            if(property.isSettable())
                property.setObject(dictionary.get(property.getName()));
        }
        return wrapped;
    }
}
