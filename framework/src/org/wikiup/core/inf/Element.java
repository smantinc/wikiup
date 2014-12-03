package org.wikiup.core.inf;

public interface Element extends Attribute {
    public Attribute getAttribute(String name);

    public Attribute addAttribute(String name);

    public void removeAttribute(Attribute attr);

    public Iterable<Attribute> getAttributes();
}
