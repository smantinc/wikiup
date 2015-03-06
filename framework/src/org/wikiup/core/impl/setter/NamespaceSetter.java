package org.wikiup.core.impl.setter;

import org.wikiup.core.exception.NamespaceException;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class NamespaceSetter<V> implements Dictionary.Mutable<V> {
    private Dictionary.Mutable<V> defaultMutable = null;
    private Map<String, Dictionary.Mutable<V>> namespaces = new HashMap<String, Dictionary.Mutable<V>>();

    public NamespaceSetter(Dictionary.Mutable<V> def) {
        defaultMutable = def;
    }

    public NamespaceSetter() {
    }

    public void addNamespace(String name, Dictionary.Mutable<V> mutable) {
        namespaces.put(name, mutable);
    }

    public boolean hasNamespace(String name) {
        return namespaces.containsKey(name);
    }

    public Dictionary.Mutable<V> getNamespaceSetter(String name) {
        return namespaces.get(name);
    }

    public void set(String name, V obj) {
        int pos = name.indexOf(':');
        Dictionary.Mutable<V> mutable = pos != -1 ? getNamespaceSetter(name.substring(0, pos)) : defaultMutable;
        Assert.isTrue(mutable != null || defaultMutable != null, NamespaceException.class, name);
        (mutable != null ? mutable : defaultMutable).set(pos != -1 && mutable != null ? name.substring(pos + 1) : name, obj);
    }

}
