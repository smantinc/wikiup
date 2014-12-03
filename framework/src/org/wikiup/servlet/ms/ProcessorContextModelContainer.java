package org.wikiup.servlet.ms;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.inf.ProcessorContext;

import java.util.Iterator;

public class ProcessorContextModelContainer implements ProcessorContext {
    private ModelProvider modelProvider;

    public ProcessorContextModelContainer(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public Object get(String name) {
        if(modelProvider != null) {
            Getter<?> getter = modelProvider.getModel(Getter.class);
            if(getter != null)
                return getter.get(name);
            Document doc = modelProvider.getModel(Document.class);
            if(doc != null) {
                Object obj = Documents.getAttributeValue(doc, name, null);
                return obj != null ? obj : Documents.getAttributeByXPath(doc, name);
            }
        }
        return null;
    }

    public ModelProvider getModelContainer(String name, Getter<?> params) {
        Document doc = modelProvider != null ? modelProvider.getModel(Document.class) : null;
        if(doc != null) {
            Document child = doc.getChild(name);
            if(child != null)
                return Interfaces.getModelContainer(child);
        }
        Getter<?> getter = modelProvider != null ? modelProvider.getModel(Getter.class) : null;
        return getter != null ? Interfaces.getModelContainer(getter.get(name)) : null;
    }

    public ModelProvider getModelContainer() {
        return modelProvider;
    }

    public void setModelContainer(ModelProvider provider) {
        modelProvider = provider;
    }

    public Iterator<ModelProvider> getIterator(String name) {
        if(modelProvider != null) {
            Document doc = modelProvider.getModel(Document.class);
            if(doc != null)
                return iterator(StringUtil.isEmpty(name) ? doc.getChildren().iterator() : doc.getChildren(name).iterator());
            Iterator<?> iterator = modelProvider.getModel(Iterator.class);
            if(iterator != null)
                return iterator(iterator);
            Iterable<?> iterable = modelProvider.getModel(Iterable.class);
            if(iterable != null)
                return iterator(iterable.iterator());
        }
        return Null.getInstance();
    }

    private Iterator<ModelProvider> iterator(final Iterator<?> iterator) {
        return new Iterator<ModelProvider>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public ModelProvider next() {
                return Interfaces.getModelContainer(iterator.next());
            }

            public void remove() {
                iterator.remove();
            }
        };
    }
}
