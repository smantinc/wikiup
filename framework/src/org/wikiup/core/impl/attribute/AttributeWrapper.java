package org.wikiup.core.impl.attribute;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;

public class AttributeWrapper implements Attribute {
    private Attribute attribute;

    public AttributeWrapper() {
        setAttribute(null);
    }

    public AttributeWrapper(Attribute attr) {
        setAttribute(attr);
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attr) {
        attribute = attr != null ? attr : Null.getInstance();
    }

    public String getName() {
        return attribute.getName();
    }

    public Object getObject() {
        return attribute.getObject();
    }

    public void setName(String name) {
        attribute.setName(name);
    }

    public void setObject(Object obj) {
        attribute.setObject(obj);
    }

    @Override
    public String toString() {
        return attribute.toString();
    }
}
