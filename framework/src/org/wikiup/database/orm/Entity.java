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

    @Override
    public void delete() {
        entity.delete();
    }

    @Override
    public Attribute get(String name) {
        return entity.get(name);
    }

    @Override
    public EntityRelatives getRelatives(String name, Getter<?> props) {
        return entity.getRelatives(name, props);
    }

    public EntityRelatives getRelatives(String name) {
        return entity.getRelatives(name, null);
    }

    @Override
    public void insert() {
        entity.insert();
    }

    @Override
    public void release() {
        entity.release();
        entity = null;
    }

    @Override
    public void select() throws InsufficientPrimaryKeys, RecordNotFoundException {
        entity.select();
    }

    @Override
    public void update() {
        entity.update();
    }

    @Override
    public Iterable<Attribute> getAttributes() {
        return entity.getAttributes();
    }

    @Override
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

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public void bind(Object object) {
        entity.bind(object);
    }
}
