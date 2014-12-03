package org.wikiup.core.impl.document;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;

import java.util.Iterator;

public class DocumentWithNamespace extends DocumentWrapper {
    private String namespace;

    public DocumentWithNamespace(Document doc, String ns) {
        super(doc);
        namespace = ns;
    }

    public DocumentWithNamespace(Document doc, String ns, String nsValue) {
        super(doc);
        namespace = ns != null ? ns : Documents.getNamespace(doc, nsValue);
        setNamespaceAttribute(doc, namespace, nsValue);
    }

    private void setNamespaceAttribute(Document doc, String ns, String nsValue) {
        if(ns.length() > 0 && ns.charAt(ns.length() - 1) == ':')
            ns = ns.substring(0, ns.length() - 1);
        Documents.setAttributeValue(doc, "xmlns" + (ns.length() > 0 ? (":" + ns) : ns), nsValue);
    }

    @Override
    public Document getChild(String name) {
        return getNamespace(super.getChild(name));
    }

    @Override
    public Iterable<Document> getChildren(String name) {
        final Iterable<Document> children = super.getChildren(name);
        final DocumentWithNamespace parent = this;
        return new Iterable<Document>() {
            public Iterator<Document> iterator() {
                return new ChildrenIterator(children.iterator(), parent);
            }
        };
    }

    @Override
    public Iterable<Document> getChildren() {
        final Iterable<Document> children = super.getChildren();
        final DocumentWithNamespace parent = this;
        return new Iterable<Document>() {
            public Iterator<Document> iterator() {
                return new ChildrenIterator(children.iterator(), parent);
            }
        };
    }

    @Override
    public Document addChild(String name) {
        return getNamespace(super.addChild(stripName(name)));
    }

    @Override
    public void removeNode(Document child) {
        super.removeNode(child instanceof DocumentWithNamespace ? ((DocumentWithNamespace) child).getDocument() : child);
    }

    @Override
    public Document getParentNode() {
        return getNamespace(super.getParentNode());
    }

    @Override
    public String getName() {
        return namespace + super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(stripName(name));
    }

    private String stripName(String name) {
        return name.startsWith(namespace) ? name.substring(namespace.length()) : name;
    }

    public Document getNamespace(Document doc) {
        return new DocumentWithNamespace(doc, namespace);
    }

    private static class ChildrenIterator implements Iterator<Document> {
        private Iterator<Document> iterator;
        private DocumentWithNamespace parent;

        public ChildrenIterator(Iterator<Document> iterator, DocumentWithNamespace parent) {
            this.iterator = iterator;
            this.parent = parent;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Document next() {
            return parent.getNamespace(iterator.next());
        }

        public void remove() {
            iterator.remove();
        }
    }
}
