package org.wikiup.core.impl.bindable;

import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.inf.Bindable;
import org.wikiup.core.inf.Getter;

public class ByPropertyNames implements Bindable {
    private Getter<?> getter;

    public ByPropertyNames(Getter<?> getter) {
        this.getter = getter;
    }

    public void bind(Object object) {
        BeanProperties properties = new BeanProperties(object);
        for(BeanProperty property : properties) {
            Object v = property.isSettable() ? getter.get(property.getName()) : null;
            if(v != null)
                property.setObject(v);
        }
    }
}
