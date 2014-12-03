package org.wikiup.core.impl.mp;


import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;

import java.util.Iterator;

public class DocumentModelProvider implements ModelProvider {
    private Document document;
    private Iterable<Document> iterable;

    public DocumentModelProvider(Document document) {
        this.document = document;
    }

    public DocumentModelProvider(Document document, Iterable<Document> iterable) {
        this.document = document;
        this.iterable = iterable;
    }

    public <E> E getModel(Class<E> clazz) {
        Object object = null;
        if(Document.class.isAssignableFrom(clazz))
            object = document;
        else if(Getter.class.equals(clazz))
            object = new DocumentModelGetter();
        else if(Iterator.class.equals(clazz))
            object = (iterable != null ? iterable : document.getChildren()).iterator();
        return Interfaces.cast(clazz, object);
    }

    @Override
    public String toString() {
        return ValueUtil.toString(document.getObject());
    }

    private class DocumentModelGetter implements Getter<Object> {
        public Object get(String name) {
            Attribute attribute = document.getAttribute(name);
            return attribute != null ? attribute : getChildDocumentModelContainer(name);
        }

        private DocumentModelProvider getChildDocumentModelContainer(String name) {
            Document doc = StringUtil.compare(document.getName(), name) ? document : document.getChild(name);
            return doc != null ? new DocumentModelProvider(doc, document.getChildren(name)) : null;
        }
    }
}
