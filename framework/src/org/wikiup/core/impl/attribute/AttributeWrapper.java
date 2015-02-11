package org.wikiup.core.impl.attribute;

import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Attribute;

public class AttributeWrapper extends WrapperImpl<Attribute> implements Attribute {

    public AttributeWrapper() {
        super(null);
    }

    public AttributeWrapper(Attribute attr) {
        super(attr);
    }

    public Attribute getAttribute() {
        return wrapped;
    }

    public void setAttribute(Attribute attr) {
        wrap(attr != null ? attr : Null.getInstance());
    }

    public String getName() {
        return wrapped.getName();
    }

    public Object getObject() {
        return wrapped.getObject();
    }

    public void setName(String name) {
        wrapped.setName(name);
    }

    public void setObject(Object obj) {
        wrapped.setObject(obj);
    }

    @Override
    public String toString() {
        return wrapped.toString();
    }
}
