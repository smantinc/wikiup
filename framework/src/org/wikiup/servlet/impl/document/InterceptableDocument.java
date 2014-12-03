package org.wikiup.servlet.impl.document;

import org.wikiup.core.impl.document.DocumentWrapper;
import org.wikiup.core.inf.Document;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.impl.iterator.InterceptableDocumentIterator;

import java.util.Iterator;

public class InterceptableDocument extends DocumentWrapper {
    private ServletProcessorContext context;

    public InterceptableDocument(ServletProcessorContext context, Document data) {
        super(data);
        this.context = context;
    }

    @Override
    public Iterable<Document> getChildren() {
        final Iterable<Document> iterable = super.getChildren();
        return new Iterable<Document>() {
            public Iterator<Document> iterator() {
                return new InterceptableDocumentIterator(iterable.iterator(), context, null);
            }
        };
    }

    @Override
    public Iterable<Document> getChildren(final String name) {
        final Iterable<Document> iterable = super.getChildren();
        return new Iterable<Document>() {
            public Iterator<Document> iterator() {
                return new InterceptableDocumentIterator(iterable.iterator(), context, name);
            }
        };
    }
}
