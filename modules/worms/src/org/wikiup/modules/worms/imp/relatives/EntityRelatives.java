package org.wikiup.modules.worms.imp.relatives;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Releasable;
import org.wikiup.modules.worms.WormsEntity;

public abstract class EntityRelatives extends org.wikiup.database.orm.EntityRelatives implements Releasable {
    private WormsEntity entity;

    abstract public void init(Document desc, WormsEntity origin, Getter<?> parameters);

    public EntityRelatives(WormsEntity node) {
        this.entity = node;
    }

    public EntityRelatives() {
    }

    public void setEntity(WormsEntity node) {
        entity = node;
    }

    public WormsEntity getEntity() {
        return entity;
    }

    public Attribute getAttribute(String name) {
        return entity.getPropertyObject(name);
    }

    public Iterable<Attribute> getAttributes() {
        return entity.getResultSet(false) != null ? new NonePropertyAttributeIterable<Attribute>(entity.getProperties()) : Null.getInstance();
    }

    public Document getChild(String name) {
        return entity.getRelatives(name, null);
    }

    public Iterable<Document> getChildren(String name) {
        return Null.getInstance();
    }

    public Iterable<Document> getChildren() {
        return Null.getInstance();
    }

    public Document addChild(String name) {
        return Null.getInstance();
    }

    public void removeNode(Document child) {
    }

    public Document getParentNode() {
        return null;
    }

    public void release() {
        entity.release();
    }
}
