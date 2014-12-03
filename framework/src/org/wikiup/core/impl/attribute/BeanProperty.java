package org.wikiup.core.impl.attribute;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.util.ContextUtil;

public class BeanProperty implements Attribute {
    private Object instance;
    private String name;

    private Class<?> propertyClass;

    public BeanProperty(Object instance, String name) {
        this(instance, name, instance.getClass());
    }

    public BeanProperty(Object instance, String name, Class<?> propertyClass) {
        this.instance = instance;
        this.name = name;
        this.propertyClass = propertyClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getObject() {
        return ContextUtil.getBeanProperty(instance, name);
    }

    public void setObject(Object obj) {
        ContextUtil.setBeanProperty(instance, name, obj);
    }

    public boolean isGettable() {
        return ContextUtil.getBeanPropertyGetMethod(instance.getClass(), name) != null;
    }

    public boolean isSettable() {
        return ContextUtil.getBeanPropertySetMethod(instance.getClass(), name, null) != null;
    }

    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    @Override
    public String toString() {
        return name;
    }
}
