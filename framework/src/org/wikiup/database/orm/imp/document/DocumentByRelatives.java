package org.wikiup.database.orm.imp.document;

import java.util.Iterator;

import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.document.DocumentWrapper;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.orm.inf.Relatives;

public class DocumentByRelatives extends DocumentWrapper {
    private String name;
    private Relatives relatives;
    
    public DocumentByRelatives(Relatives relatives) {
        this("relative", relatives);
    }

    public DocumentByRelatives(String name, Relatives relatives) {
        this.relatives = relatives;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Attribute getAttribute(String name) {
        return relatives.get(name);
    }

    @Override
    public Iterable<Attribute> getAttributes() {
        return relatives.getProperties();
    }
    
    public Iterable<Document> getChildren() {
        final Iterable<Relatives> iterable = Interfaces.cast(Iterable.class, relatives);
        if(iterable == null)
            return Null.getInstance();
        return new Iterable<Document>() {
            @Override
            public Iterator<Document> iterator() {
                return new IteratorImpl(iterable.iterator());
            }
        };
    }
    
    private static class IteratorImpl implements Iterator<Document> {
        private Iterator<Relatives> iterator;

        public IteratorImpl(Iterator<Relatives> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Document next() {
            Relatives relatives = iterator.next();
            return new DocumentByRelatives(relatives);
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
