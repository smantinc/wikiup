package org.wikiup.core.impl.document;

import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.element.ElementWrapper;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Provider;

public class DocumentWrapper extends ElementWrapper implements Document, Provider<Document> {
    private Document document;

    public DocumentWrapper() {
        setDocument(null);
    }

    public DocumentWrapper(Document doc) {
        super(doc);
        setDocument(doc);
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document doc) {
        super.setElement(doc);
        document = doc != null ? doc : Null.getInstance();
    }

    public Document addChild(String name) {
        return document.addChild(name);
    }

    public void removeNode(Document child) {
        document.removeNode(child);
    }

    public Document getChild(String name) {
        return document.getChild(name);
    }

    public Iterable<Document> getChildren(String name) {
        return document.getChildren(name);
    }

    public Iterable<Document> getChildren() {
        return document.getChildren();
    }

    public Document getParentNode() {
        return document.getParentNode();
    }

    public Document get() {
        return document;
    }
}
