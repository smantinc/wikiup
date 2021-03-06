package org.wikiup.core.impl.attribute;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.util.Dictionaries;

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
        return Dictionaries.getBeanProperty(instance, name);
    }

    public void setObject(Object obj) {
        Dictionaries.setBeanProperty(instance, name, obj);
    }

    public boolean isGettable() {
        return Dictionaries.getBeanPropertyGetMethod(instance.getClass(), name) != null;
    }

    public boolean isSettable() {
        return Dictionaries.getBeanPropertySetMethod(instance.getClass(), name, null) != null;
    }

    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    @Override
    public String toString() {
        return name;
    }
}
