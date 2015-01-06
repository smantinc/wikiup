package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.document.MergedDocument;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Wirable;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ToDocumentWirable<T> extends WrapperImpl<Factory.ByName<T>> implements Factory.ByNameToWirable<T, Document>, Iterable<String> {
    private final Map<String, Node> nodes;
    
    public ToDocumentWirable(Factory.ByName<T> factory) {
        super(factory);
        nodes = new HashMap<String, Node>();
    }

    public ToDocumentWirable(Factory.ByName<T> factory, Map<String, Node> nodes) {
        super(factory);
        this.nodes = nodes;
    }

    @Override
    public Wirable<T, Document> build(String className) {
        Node node = nodes.get(className);
        T bean = wrapped.build(className);
        Wirable.ByDocument wirable = Interfaces.cast(Wirable.ByDocument.class, bean);
        return wirable != null ? new MergedDocumentWirable<T>(bean, node.document, wirable) : new MergedDocumentWirable<T>(bean, node.document);
    }

    @Override
    public Iterator<String> iterator() {
        return nodes.keySet().iterator();
    }

    private static class Node {
        public String name;
        public Document document;

        public Node(String name, Document document) {
            this.name = name;
            this.document = document;
        }
    }
    
    private static final class MergedDocumentWirable<T> extends WrapperImpl<T> implements Wirable<T, Document> {
        private Document merging;
        private Wirable<T, Document> wirable;

        public MergedDocumentWirable(T wrapped, Document merging) {
            this(wrapped, merging, null);
        }
        
        public MergedDocumentWirable(T wrapped, Document merging, Wirable<T, Document> wirable) {
            super(wrapped);
            this.merging = merging;
            this.wirable = wirable;
        }

        @Override
        public T wire(Document desc) {
            return wirable != null ? wirable.wire(new MergedDocument(desc, merging)) : wrapped;
        }
    }
    
    public static final class WIRABLE<T> extends WrapperImpl<ToDocumentWirable<T>> implements Wirable.ByDocument<ToDocumentWirable<T>> {
        public WIRABLE(ToDocumentWirable<T> wrapped) {
            super(wrapped);
        }

        @Override
        public ToDocumentWirable<T> wire(Document desc) {
            for(Document node : desc.getChildren()) {
                String id = Documents.getId(node);
                wrapped.nodes.put(id, new Node(id, node));
            }
            return wrapped;
        }
    }
}
