package org.wikiup.core.impl.dictionary;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.ContextUtil;

public class BeanPropertyDictionary implements Dictionary<Object> {
    private Object bean;

    public BeanPropertyDictionary() {
        this.bean = this;
    }

    public BeanPropertyDictionary(Object bean) {
        this.bean = bean;
    }

    public Object get(String name) {
        return ContextUtil.getBeanProperty(bean, name);
    }
}
