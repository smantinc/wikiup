package org.wikiup.modules.velocity.context;

import org.apache.velocity.context.Context;
import org.wikiup.core.inf.Document;

import java.util.Collection;
import java.util.Iterator;

public class DocumentContextCollection implements Collection<Context> {
    private Document document;

    public DocumentContextCollection(Document document) {
        this.document = document;
    }

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean contains(Object o) {
        return false;
    }

    public Iterator<Context> iterator() {
        final Iterator<Document> iterator = document.getChildren().iterator();
        return new Iterator<Context>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Context next() {
                return new WikiupDocumentVelocityContext(iterator.next());
            }

            public void remove() {
                iterator.remove();
            }
        };
    }

    public Object[] toArray() {
        return new Object[0];
    }

    public <T> T[] toArray(T[] a) {
        return null;
    }

    public boolean add(Context document) {
        return false;
    }

    public boolean remove(Object o) {
        return false;
    }

    public boolean containsAll(Collection<?> c) {
        return false;
    }

    public boolean addAll(Collection<? extends Context> c) {
        return false;
    }

    public boolean removeAll(Collection<?> c) {
        return false;
    }

    public boolean retainAll(Collection<?> c) {
        return false;
    }

    public void clear() {
    }
}
