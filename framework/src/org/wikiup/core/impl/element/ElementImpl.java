package org.wikiup.core.impl.element;

import org.wikiup.core.impl.attribute.AttributeImpl;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Element;

public class ElementImpl extends AttributeImpl implements Element {
    private DomContainer<Attribute> attributes = new DomContainer<Attribute>();

    public ElementImpl() {
        super();
    }

    public ElementImpl(String name) {
        super(name);
    }

    public ElementImpl(String name, Object value) {
        super(name, value);
    }

    public Attribute getAttribute(String name) {
        return attributes.get(name);
    }

    public Attribute addAttribute(String name) {
        return attributes.add(name, new AttributeImpl(name));
    }

    public void addAttribute(String name, Attribute attr) {
        attributes.add(name, attr);
    }

    public void removeAttribute(Attribute attr) {
        attributes.remove(attr.getName(), attr);
    }

    public Iterable<Attribute> getAttributes() {
        return attributes.iterable();
    }

    public int attributeCount() {
        return attributes.size();
    }

}
