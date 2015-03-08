package org.wikiup.core.impl.dictionary;

import org.wikiup.core.exception.NamespaceException;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.core.util.StringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NamespaceDictionary implements Dictionary<Object>, Iterable<String> {
    private Map<String, Dictionary<?>> namespace = new HashMap<String, Dictionary<?>>();
    private boolean mute = true;

    public NamespaceDictionary() {
    }

    public NamespaceDictionary(boolean mute) {
        this.mute = mute;
    }

    public NamespaceDictionary addNamespace(String name, Dictionary<?> dictionary) {
        namespace.put(name, dictionary);
        return this;
    }

    public void removeNamespace(String name) {
        namespace.remove(name);
    }

    public boolean hasNamespace(String name) {
        return namespace.containsKey(name);
    }

    public Dictionary<?> getNamespace(String name) {
        return namespace.get(name);
    }

    public Object get(String name) {
        String[] path = StringUtil.splitNamespaces(name);
        Dictionary<?> dictionary = getNamespace(path[0]);
        Assert.isTrue(dictionary != null || mute, NamespaceException.class, name);
        return dictionary != null ? Dictionaries.getProperty(dictionary, path, path.length, 1) : null;
    }

    public Iterator<String> iterator() {
        return namespace.keySet().iterator();
    }
}
