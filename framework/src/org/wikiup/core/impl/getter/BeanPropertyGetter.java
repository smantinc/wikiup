package org.wikiup.core.impl.getter;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.ContextUtil;

public class BeanPropertyGetter implements Getter<Object> {
    private Object bean;

    public BeanPropertyGetter() {
        this.bean = this;
    }

    public BeanPropertyGetter(Object bean) {
        this.bean = bean;
    }

    public Object get(String name) {
        return ContextUtil.getBeanProperty(bean, name);
    }
}
