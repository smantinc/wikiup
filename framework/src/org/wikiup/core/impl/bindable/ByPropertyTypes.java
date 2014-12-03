package org.wikiup.core.impl.bindable;

import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.inf.Bindable;
import org.wikiup.core.inf.ModelProvider;

public class ByPropertyTypes implements Bindable {
    private ModelProvider modelProvider;

    public ByPropertyTypes(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public void bind(Object object) {
        BeanProperties properties = new BeanProperties(object);
        for(BeanProperty property : properties) {
            Object v = property.isSettable() ? modelProvider.getModel(property.getPropertyClass()) : null;
            if(v != null)
                property.setObject(v);
        }
    }
}
