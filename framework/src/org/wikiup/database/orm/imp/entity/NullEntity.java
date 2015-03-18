package org.wikiup.database.orm.imp.entity;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.database.orm.Entity;
import org.wikiup.database.orm.inf.Relatives;

public class NullEntity extends Entity {
    public NullEntity() {
        super(null);
    }

    @Override
    public void delete() {
    }

    @Override
    public Attribute get(String name) {
        return Null.getInstance();
    }

    @Override
    public Relatives getRelatives(String name, Dictionary<?> props) {
        return new org.wikiup.database.orm.imp.entity.NullEntityRelatives();
    }

    @Override
    public void insert() {
    }

    @Override
    public void release() {
    }

    @Override
    public void select() {
    }

    @Override
    public void update() {
    }

    @Override
    public Iterable<Attribute> getAttributes() {
        return Null.getInstance().iter();
    }
}
