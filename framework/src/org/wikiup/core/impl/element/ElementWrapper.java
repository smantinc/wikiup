package org.wikiup.core.impl.element;

import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.attribute.AttributeWrapper;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Element;

public class ElementWrapper extends AttributeWrapper implements Element {
    private Element element;

    public ElementWrapper() {
        setElement(null);
    }

    public ElementWrapper(Element element) {
        super(element);
        setElement(element);
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        super.setAttribute(element);
        this.element = element != null ? element : Null.getInstance();
    }

    public Attribute getAttribute(String name) {
        return element.getAttribute(name);
    }

    public Attribute addAttribute(String name) {
        return element.addAttribute(name);
    }

    public void removeAttribute(Attribute attr) {
        element.removeAttribute(attr);
    }

    public Iterable<Attribute> getAttributes() {
        return element.getAttributes();
    }
}
