package org.wikiup.core.impl.context;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Documents;

public class XPathContext implements Context<Attribute, Object> {
    private Document document;

    public XPathContext() {
    }

    public XPathContext(Document document) {
        this.document = document != null ? document : Null.getInstance();
    }

    public Attribute get(String name) {
        return getNode(name);
    }

    public void set(String name, Object value) {
        Attribute vs = getNode(name);
        (vs != null ? vs : name.charAt(0) == '@' ? document.addAttribute(name.substring(1)) : document.addChild(name)).setObject(value);
    }

    private Attribute getNode(String name) {
        Attribute vs = document.getAttribute(name);
        if(vs == null)
            vs = document.getChild(name);
        return vs != null ? vs : Documents.getAttributeByXPath(document, name);
    }
}
