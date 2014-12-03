package org.wikiup.core.impl.getter.dl;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.DocumentSelector;
import org.wikiup.core.util.Documents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ByAttributeNameSelector implements DocumentSelector, DocumentAware, Iterable<String> {
    private String attributeName = "name";
    private String childName;
    private Map<String, Document> documents = new HashMap<String, Document>();

    public ByAttributeNameSelector(Document doc, String attributeName, String childName) {
        this.childName = childName;
        this.attributeName = attributeName;
        aware(doc);
    }

    public ByAttributeNameSelector(Document doc, String name) {
        this(doc, name, null);
    }

    public Document get(String name) {
        return documents.get(name);
    }

    public void aware(Document desc) {
        for(Document doc : childName != null ? desc.getChildren(childName) : desc.getChildren())
            documents.put(Documents.getAttributeValue(doc, attributeName, null), doc);
    }

    public Iterator<String> iterator() {
        return documents.keySet().iterator();
    }
}