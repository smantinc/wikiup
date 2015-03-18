package org.wikiup.modules.worms.imp.relatives;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Releasable;
import org.wikiup.database.orm.inf.Relatives;
import org.wikiup.modules.worms.WormsEntity;

public abstract class EntityRelatives implements Relatives, Releasable {
    private WormsEntity entity;

    abstract public void init(Document desc, WormsEntity origin, Dictionary<?> parameters);

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

    public Attribute get(String name) {
        return entity.getPropertyObject(name);
    }

    public Iterable<Attribute> getAttributes() {
        return entity.getResultSet(false) != null ? new NonePropertyAttributeIterable<Attribute>(entity.getProperties()) : Null.getInstance();
    }

    public void release() {
        entity.release();
    }
}
