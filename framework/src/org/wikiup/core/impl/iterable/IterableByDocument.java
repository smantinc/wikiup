package org.wikiup.core.impl.iterable;

import java.util.Iterator;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Translator;

public class IterableByDocument extends WrapperImpl<Document> implements Iterable<Object> {
    public IterableByDocument(Document wrapped) {
        super(wrapped);
    }

    @Override
    public Iterator<Object> iterator() {
        return new DocumentAttributeIterator(wrapped.getAttributes().iterator());
    }
    
    private static class DocumentAttributeIterator implements Iterator<Object> {
        private Iterator<Attribute> iterator;
        
        public DocumentAttributeIterator(Iterator<Attribute> iterator) {
            this.iterator = iterator;
        }
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Object next() {
            Attribute attribute = iterator.next();
            return attribute.getObject();
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
    
    public static final class TRANSLATOR implements Translator<Document, IterableByDocument> {
        @Override
        public IterableByDocument translate(Document doc) {
            return new IterableByDocument(doc);
        }
    }
}
