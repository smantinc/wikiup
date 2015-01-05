package org.wikiup.core.impl.bindable;

import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.inf.Bindable;
import org.wikiup.core.inf.BeanFactory;

public class ByPropertyTypes implements Bindable {
    private BeanFactory modelProvider;

    public ByPropertyTypes(BeanFactory modelProvider) {
        this.modelProvider = modelProvider;
    }

    public void bind(Object object) {
        BeanProperties properties = new BeanProperties(object);
        for(BeanProperty property : properties) {
            Object v = property.isSettable() ? modelProvider.query(property.getPropertyClass()) : null;
            if(v != null)
                property.setObject(v);
        }
    }
}
