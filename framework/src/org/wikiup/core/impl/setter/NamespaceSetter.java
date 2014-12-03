package org.wikiup.core.impl.setter;

import org.wikiup.core.exception.NamespaceException;
import org.wikiup.core.inf.Setter;
import org.wikiup.core.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class NamespaceSetter<V> implements Setter<V> {
    private Setter<V> defaultSetter = null;
    private Map<String, Setter<V>> namespaces = new HashMap<String, Setter<V>>();

    public NamespaceSetter(Setter<V> def) {
        defaultSetter = def;
    }

    public NamespaceSetter() {
    }

    public void addNamespace(String name, Setter<V> setter) {
        namespaces.put(name, setter);
    }

    public boolean hasNamespace(String name) {
        return namespaces.containsKey(name);
    }

    public Setter<V> getNamespaceSetter(String name) {
        return namespaces.get(name);
    }

    public void set(String name, V obj) {
        int pos = name.indexOf(':');
        Setter<V> setter = pos != -1 ? getNamespaceSetter(name.substring(0, pos)) : defaultSetter;
        Assert.isTrue(setter != null || defaultSetter != null, NamespaceException.class, name);
        (setter != null ? setter : defaultSetter).set(pos != -1 && setter != null ? name.substring(pos + 1) : name, obj);
    }

}
