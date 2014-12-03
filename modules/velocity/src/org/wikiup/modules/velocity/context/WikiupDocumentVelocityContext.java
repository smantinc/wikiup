package org.wikiup.modules.velocity.context;

import org.apache.velocity.context.Context;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.ValueUtil;

public class WikiupDocumentVelocityContext implements Context {
    private Document document;

    public WikiupDocumentVelocityContext(Document doc) {
        document = doc;
    }

    public boolean containsKey(Object object) {
        String name = object.toString();
        return document.getAttribute(name) != null || document.getChild(name) != null;
    }

    public Object get(String s) {
        Object obj = document.getAttribute(s);
        Document doc = obj == null ? document.getChild(s) : null;
        return obj == null ? (doc == null ? null : new WikiupDocumentVelocityContext(doc)) : obj;
    }

    public Object[] getKeys() {
        return null;
    }

    public Object put(String s, Object obj) {
        Documents.touchAttribute(document, s).setObject(obj);
        return obj;
    }

    public Object remove(Object obj) {
        return obj;
    }

    @Override
    public String toString() {
        return ValueUtil.toString(document);
    }
}
