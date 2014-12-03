package org.wikiup.core.inf;

public interface Document extends Element {
    public Document getChild(String name);

    public Document addChild(String name);

    public Iterable<Document> getChildren(String name);

    public Iterable<Document> getChildren();

    public void removeNode(Document child);

    public Document getParentNode();
}
