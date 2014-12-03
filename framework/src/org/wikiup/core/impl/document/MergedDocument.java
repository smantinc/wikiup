package org.wikiup.core.impl.document;

import org.wikiup.core.impl.iterable.IterableCollection;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;

public class MergedDocument extends DocumentWrapper {
    private Document other;

    public MergedDocument(Document doc1, Document doc2) {
        super(doc1);
        other = doc2;
    }

    @Override
    public Document getChild(String name) {
        Document child = super.getChild(name);
        return child != null ? child : other.getChild(name);
    }

    @Override
    public Attribute getAttribute(String name) {
        Attribute value = super.getAttribute(name);
        return value != null ? value : other.getAttribute(name);
    }

    @Override
    public Iterable<Document> getChildren() {
        return new IterableCollection<Document>(other.getChildren(), super.getChildren());
    }

    @Override
    public Iterable<Document> getChildren(String name) {
        return new IterableCollection<Document>(other.getChildren(name), super.getChildren(name));
    }

    @Override
    public Iterable<Attribute> getAttributes() {
        return new IterableCollection<Attribute>(other.getAttributes(), super.getAttributes());
    }

    @Override
    public Object getObject() {
        Object obj = super.getObject();
        return obj != null ? obj : other.getObject();
    }
}
