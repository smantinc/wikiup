package org.wikiup.core.impl.attribute;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.util.ValueUtil;

import java.util.Map;

public class MapAttribute implements Attribute {
    private String key;
    private Map<String, Object> map;

    public MapAttribute(Map<String, Object> map, String key) {
        this.key = key;
        this.map = map;
    }

    public String getName() {
        return key;
    }

    public void setName(String name) {
        key = name;
    }

    public Object getObject() {
        return map.get(key);
    }

    public void setObject(Object obj) {
        map.put(key, obj);
    }

    @Override
    public String toString() {
        return ValueUtil.toString(getObject());
    }
}