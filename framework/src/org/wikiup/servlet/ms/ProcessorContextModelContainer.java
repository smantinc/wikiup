package org.wikiup.servlet.ms;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.inf.ProcessorContext;

import java.util.Iterator;

public class ProcessorContextModelContainer implements ProcessorContext {
    private BeanContainer modelProvider;

    public ProcessorContextModelContainer(BeanContainer modelProvider) {
        this.modelProvider = modelProvider;
    }

    public Object get(String name) {
        if(modelProvider != null) {
            Dictionary<?> dictionary = modelProvider.query(Dictionary.class);
            if(dictionary != null)
                return dictionary.get(name);
            Document doc = modelProvider.query(Document.class);
            if(doc != null) {
                Object obj = Documents.getAttributeValue(doc, name, null);
                return obj != null ? obj : Documents.getAttributeByXPath(doc, name);
            }
        }
        return null;
    }

    public BeanContainer getModelContainer(String name, Dictionary<?> params) {
        Document doc = modelProvider != null ? modelProvider.query(Document.class) : null;
        if(doc != null) {
            Document child = doc.getChild(name);
            if(child != null)
                return Interfaces.getModelContainer(child);
        }
        Dictionary<?> dictionary = modelProvider != null ? modelProvider.query(Dictionary.class) : null;
        return dictionary != null ? Interfaces.getModelContainer(dictionary.get(name)) : null;
    }

    public BeanContainer getModelContainer() {
        return modelProvider;
    }

    public void setModelContainer(BeanContainer provider) {
        modelProvider = provider;
    }

    public Iterator<BeanContainer> getIterator(String name) {
        if(modelProvider != null) {
            Document doc = modelProvider.query(Document.class);
            if(doc != null)
                return iterator(StringUtil.isEmpty(name) ? doc.getChildren().iterator() : doc.getChildren(name).iterator());
            Iterator<?> iterator = modelProvider.query(Iterator.class);
            if(iterator != null)
                return iterator(iterator);
            Iterable<?> iterable = modelProvider.query(Iterable.class);
            if(iterable != null)
                return iterator(iterable.iterator());
        }
        return Null.getInstance();
    }

    private Iterator<BeanContainer> iterator(final Iterator<?> iterator) {
        return new Iterator<BeanContainer>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public BeanContainer next() {
                return Interfaces.getModelContainer(iterator.next());
            }

            public void remove() {
                iterator.remove();
            }
        };
    }
}
