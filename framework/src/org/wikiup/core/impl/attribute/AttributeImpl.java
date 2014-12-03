package org.wikiup.core.impl.attribute;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.util.ValueUtil;

public class AttributeImpl implements Attribute {
    private String name;
    private Object object;

    public AttributeImpl() {
    }

    public AttributeImpl(String name) {
        this.name = name;
    }

    public AttributeImpl(String name, Object object) {
        this.name = name;
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public Object getObject() {
        return object;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setObject(Object obj) {
        object = obj;
    }

    @Override
    public String toString() {
        return ValueUtil.toString(object);
    }
}
