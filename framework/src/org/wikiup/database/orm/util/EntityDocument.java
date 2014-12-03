package org.wikiup.database.orm.util;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.database.orm.inf.EntityModel;

public class EntityDocument implements Document, Getter<Attribute> {
    private EntityModel entity;
    private String name = "entity";

    public EntityDocument(EntityModel entity) {
        this.entity = entity;
    }

    public Iterable<Document> getChildren() {
        return Null.getInstance().iter();
    }

    public Iterable<Document> getChildren(String name) {
        return Null.getInstance().iter();
    }

    public Document getChild(String name) {
        return entity.getRelatives(name, null);
    }

    public Document addChild(String name) {
        return Null.getInstance();
    }

    public void removeNode(Document child) {
    }

    public Document getParentNode() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getObject() {
        return null;
    }

    public void setObject(Object obj) {
    }

    public Attribute getAttribute(String name) {
        return entity.get(name);
    }

    public Iterable<Attribute> getAttributes() {
        return entity.getAttributes();
    }

    public Attribute addAttribute(String name) {
        return Null.getInstance();
    }

    public void removeAttribute(Attribute attrValue) {
    }

    public Attribute get(String name) {
        return getAttribute(name);
    }
}
