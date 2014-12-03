package org.wikiup.core.impl.context;

import org.wikiup.core.impl.getter.BeanPropertyGetter;
import org.wikiup.core.impl.setter.BeanPropertySetter;

public class BeanPropertyContext extends ContextWrapper<Object, Object> {
    public BeanPropertyContext(Object bean) {
        super(new BeanPropertyGetter(bean), new BeanPropertySetter(bean));
    }
}
