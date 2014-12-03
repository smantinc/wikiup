package org.wikiup.modules.jython.orm;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Getter;
import org.wikiup.database.orm.Entity;
import org.wikiup.database.orm.EntityRelatives;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.database.orm.inf.PersistentOperationInf;

public class PythonEntity implements PersistentOperationInf, EntityModel {
    private Entity entity;

    public PythonEntity(Entity entity) {
        this.entity = entity;
    }

    public Object __getitem__(String name) {
        return entity.get(name);
    }

    public void __setitem__(String name, Object value) {
        entity.set(name, value);
    }

    public Object __getattr__(String name) {
        return entity.get(name);
    }

    public void __setattr__(String name, Object value) {
        entity.set(name, value);
    }

    public void select() {
        entity.select();
    }

    public void update() {
        entity.update();
    }

    public void delete() {
        entity.delete();
    }

    public void insert() {
        entity.insert();
    }

    public String getName() {
        return entity.getName();
    }

    public EntityRelatives getRelatives(String name) {
        return entity.getRelatives(name, null);
    }

    public EntityRelatives getRelatives(String name, Getter<String> getter) {
        return entity.getRelatives(name, getter);
    }

    public Iterable<Attribute> getAttributes() {
        return entity.getAttributes();
    }

    public void bind(Object object) {
        entity.bind(object);
    }

    public Attribute get(String name) {
        return entity.get(name);
    }

    public void release() {
        entity.release();
    }
}