package org.wikiup.database.orm.imp.entity;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.database.orm.EntityRelatives;

public class NullEntityRelatives extends EntityRelatives {
    public Document addChild(String name) {
        return Null.getInstance();
    }

    public void removeNode(Document child) {
    }

    public Attribute getAttribute(String name) {
        return Null.getInstance();
    }

    public Iterable<Attribute> getAttributes() {
        return Null.getInstance().iter();
    }

    public Document getChild(String name) {
        return null;
    }

    public Iterable<Document> getChildren() {
        return Null.getInstance().iter();
    }

    public Iterable<Document> getChildren(String name) {
        return Null.getInstance().iter();
    }

    public Document getParentNode() {
        return null;
    }
}
