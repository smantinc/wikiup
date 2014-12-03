package org.wikiup.modules.lucene.util;

import org.apache.lucene.document.Field;
import org.wikiup.core.impl.document.DocumentWrapper;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;

import java.io.Reader;

public class IndexDocument extends DocumentWrapper {

    public IndexDocument() {
        super(Documents.create("document"));
    }

    public IndexDocument(Document doc) {
        super(doc);
    }

    public void addField(String name, String value, boolean isPrimaryKey, boolean store, Field.Index index) {
        Document doc = addChild("field");
        Documents.setAttributeValue(doc, "id", name);
        Documents.setAttributeValue(doc, "value", value);
        if(isPrimaryKey)
            Documents.setAttributeValue(doc, "lucene:primary-key", isPrimaryKey);
        if(!store)
            Documents.setAttributeValue(doc, "lucene:store", store);
        if(index != null)
            Documents.setAttributeValue(doc, "lucene:index", index.toString());
    }

    public void addField(String name, String value) {
        addField(name, value, false, true, null);
    }

    public void addField(String name, Reader reader) {
        Document doc = addChild("field");
        Documents.setAttributeValue(doc, "id", name);
        Documents.setAttributeValue(doc, "reader", reader);
    }
}
