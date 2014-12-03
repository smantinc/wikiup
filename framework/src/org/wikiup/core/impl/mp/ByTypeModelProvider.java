package org.wikiup.core.impl.mp;

import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.util.Interfaces;

public class ByTypeModelProvider implements ModelProvider {
    private Object instance;

    public ByTypeModelProvider(Object instance) {
        this.instance = instance;
    }

    public <E> E getModel(Class<E> clazz) {
        Iterable<BeanProperty> iterable = new BeanProperties(instance);
        for(BeanProperty property : iterable)
            if(clazz.isAssignableFrom(property.getPropertyClass()) && property.isGettable())
                return Interfaces.cast(clazz, property.getObject());
        return null;
    }
}
