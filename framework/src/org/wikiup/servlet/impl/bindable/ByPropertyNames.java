package org.wikiup.servlet.impl.bindable;

import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.inf.Bindable;
import org.wikiup.core.inf.Dictionary;

public class ByPropertyNames implements Bindable {
    private Dictionary<?> dictionary;

    public ByPropertyNames(Dictionary<?> dictionary) {
        this.dictionary = dictionary;
    }

    public void bind(Object object) {
        BeanProperties properties = new BeanProperties(object);
        for(BeanProperty property : properties) {
            Object v = property.isSettable() ? dictionary.get(property.getName()) : null;
            if(v != null)
                property.setObject(v);
        }
    }
}
