package org.wikiup.database.orm;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;

import java.util.Iterator;

public abstract class EntityRelatives implements Document, Iterable<Document> {
    public Attribute addAttribute(String name) {
        return Null.getInstance();
    }

    public void removeAttribute(Attribute attrValue) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return "relative";
    }

    public Object getObject() {
        return null;
    }

    public void setObject(Object obj) {
    }

    public Iterator<Document> iterator() {
        return getChildren().iterator();
    }
}
