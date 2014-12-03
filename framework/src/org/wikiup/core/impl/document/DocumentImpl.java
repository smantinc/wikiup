package org.wikiup.core.impl.document;

import org.wikiup.core.impl.element.DomContainer;
import org.wikiup.core.impl.element.ElementImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;

import java.io.StringWriter;

public class DocumentImpl extends ElementImpl implements Document {
    private DomContainer<Document> children = new DomContainer<Document>();

    private Document parent;

    public DocumentImpl() {
        super("");
        parent = null;
    }

    public DocumentImpl(String name) {
        super(name);
        parent = null;
    }

    public DocumentImpl(Document parent) {
        super("");
        this.parent = parent;
    }

    public DocumentImpl(Document parent, String name) {
        super(name);
        this.parent = parent;
    }

    public DocumentImpl addChild(String name) {
        return (DocumentImpl) children.add(name, createDocument(name));
    }

    public void addChild(String name, Document doc) {
        children.add(name, doc);
    }

    public void attachChild(Document doc) {
        String name = doc.getName();
        DocumentImpl child = (DocumentImpl) children.get(name);
        if(child == null)
            children.add(name, doc);
        else
            for(Document node : doc.getChildren())
                child.attachChild(node);
    }

    public void addChild(Document doc) {
        addChild(doc.getName(), doc);
    }

    protected DocumentImpl createDocument(String name) {
        return new DocumentImpl(this, name);
    }

    public void removeNode(Document child) {
        children.remove(child.getName(), child);
    }

    public Document getChild(String name) {
        return children.get(name);
    }

    public Iterable<Document> getChildren(String name) {
        return children.iterable(name);
    }

    public Iterable<Document> getChildren() {
        return children.iterable();
    }

    public Document getParentNode() {
        return parent;
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        Documents.printToWriter(this, writer);
        return writer.toString();
    }
}
