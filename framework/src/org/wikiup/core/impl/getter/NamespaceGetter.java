package org.wikiup.core.impl.getter;

import org.wikiup.core.exception.NamespaceException;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.StringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NamespaceGetter implements Getter<Object>, Iterable<String> {
    private Map<String, Getter<?>> namespace = new HashMap<String, Getter<?>>();
    private boolean mute = true;

    public NamespaceGetter() {
    }

    public NamespaceGetter(boolean mute) {
        this.mute = mute;
    }

    public NamespaceGetter addNamespace(String name, Getter<?> getter) {
        namespace.put(name, getter);
        return this;
    }

    public void removeNamespace(String name) {
        namespace.remove(name);
    }

    public boolean hasNamespace(String name) {
        return namespace.containsKey(name);
    }

    public Getter<?> getNamespace(String name) {
        return namespace.get(name);
    }

    public Object get(String name) {
        String[] path = StringUtil.splitNamespaces(name);
        Getter<?> getter = getNamespace(path[0]);
        Assert.isTrue(getter != null || mute, NamespaceException.class, name);
        return getter != null ? ContextUtil.getProperty(getter, path, path.length, 1) : null;
    }

    public Iterator<String> iterator() {
        return namespace.keySet().iterator();
    }
}
