package org.wikiup.core.impl.iterable;

import org.wikiup.core.impl.attribute.BeanProperty;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BeanPropertyNames implements Iterable<String> {
    private Set<String> names = new HashSet<String>();

    public BeanPropertyNames(Class<?> clazz, boolean onlyDeclaredMethod) {
        BeanProperties properties = new BeanProperties(clazz, onlyDeclaredMethod);
        for(BeanProperty property : properties)
            names.add(property.getName());
    }

    public BeanPropertyNames(Class<?> clazz) {
        this(clazz, false);
    }

    public Iterator<String> iterator() {
        return names.iterator();
    }
}
