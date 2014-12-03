package org.wikiup.database.orm;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.database.exception.InsufficientPrimaryKeys;
import org.wikiup.database.exception.RecordNotFoundException;
import org.wikiup.database.orm.inf.EntityModel;

public class Entity implements EntityModel, Context<Attribute, Object> {
    private EntityModel entity;
    private boolean dirty;

    public Entity(EntityModel entity) {
        this.entity = entity;
    }

    public void delete() {
        entity.delete();
    }

    public Attribute get(String name) {
        return entity.get(name);
    }

    public EntityRelatives getRelatives(String name, Getter<String> getter) {
        return entity.getRelatives(name, getter);
    }

    public EntityRelatives getRelatives(String name) {
        return entity.getRelatives(name, null);
    }

    public void insert() {
        entity.insert();
    }

    public void release() {
        entity.release();
        entity = null;
    }

    public void select() throws InsufficientPrimaryKeys, RecordNotFoundException {
        entity.select();
    }

    public void update() {
        entity.update();
    }

    public Iterable<Attribute> getAttributes() {
        return entity.getAttributes();
    }

    public void set(String name, Object value) {
        Attribute attribute = get(name);
        if(attribute != null)
            attribute.setObject(value);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public String getName() {
        return entity.getName();
    }

    public void bind(Object object) {
        entity.bind(object);
    }
}
