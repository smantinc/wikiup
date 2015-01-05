package org.wikiup.servlet.ms;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.inf.ProcessorContext;

import java.util.Iterator;

public class ProcessorContextModelContainer implements ProcessorContext {
    private BeanFactory modelProvider;

    public ProcessorContextModelContainer(BeanFactory modelProvider) {
        this.modelProvider = modelProvider;
    }

    public Object get(String name) {
        if(modelProvider != null) {
            Getter<?> getter = modelProvider.query(Getter.class);
            if(getter != null)
                return getter.get(name);
            Document doc = modelProvider.query(Document.class);
            if(doc != null) {
                Object obj = Documents.getAttributeValue(doc, name, null);
                return obj != null ? obj : Documents.getAttributeByXPath(doc, name);
            }
        }
        return null;
    }

    public BeanFactory getModelContainer(String name, Getter<?> params) {
        Document doc = modelProvider != null ? modelProvider.query(Document.class) : null;
        if(doc != null) {
            Document child = doc.getChild(name);
            if(child != null)
                return Interfaces.getModelContainer(child);
        }
        Getter<?> getter = modelProvider != null ? modelProvider.query(Getter.class) : null;
        return getter != null ? Interfaces.getModelContainer(getter.get(name)) : null;
    }

    public BeanFactory getModelContainer() {
        return modelProvider;
    }

    public void setModelContainer(BeanFactory provider) {
        modelProvider = provider;
    }

    public Iterator<BeanFactory> getIterator(String name) {
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

    private Iterator<BeanFactory> iterator(final Iterator<?> iterator) {
        return new Iterator<BeanFactory>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public BeanFactory next() {
                return Interfaces.getModelContainer(iterator.next());
            }

            public void remove() {
                iterator.remove();
            }
        };
    }
}
