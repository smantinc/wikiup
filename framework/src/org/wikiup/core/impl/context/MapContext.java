package org.wikiup.core.impl.context;

import org.wikiup.core.inf.ext.Context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapContext<V> implements Context<V, V>, Iterable<String> {
    private Map<String, V> map;

    public MapContext() {
        map = new HashMap<String, V>();
    }

    public MapContext(Map<String, V> data) {
        map = data;
    }

    public V get(String name) {
        return map.get(name);
    }

    public void set(String name, V value) {
        map.put(name, value);
    }

    public Map<String, V> getMap() {
        return map;
    }

    public Iterator<String> iterator() {
        return map.keySet().iterator();
    }
}
