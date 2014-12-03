package org.wikiup.core.impl.document;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;

public class DocumentArray extends DocumentWrapper {

    public DocumentArray() {
    }

    public DocumentArray(Document doc) {
        super(doc);
    }

    @Override
    public Attribute getAttribute(String name) {
        return null;
    }

    @Override
    public Iterable<Attribute> getAttributes() {
        return Null.getInstance();
    }
}
