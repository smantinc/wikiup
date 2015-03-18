package org.wikiup.modules.ibatis.entity;


import org.wikiup.core.inf.Dictionary;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.database.orm.inf.Relatives;

public abstract class AbstractEntity implements EntityModel {
    private String name;

    public AbstractEntity(String name) {
        this.name = name;
    }

    public void bind(Object object) {
    }

    public void delete() {
    }

    public Relatives getRelatives(String name, Dictionary<?> dictionary) {
        return null;
    }

    public void insert() {
    }

    public void release() {
    }

    public void select() {
    }

    public void update() {
    }

    public String getName() {
        return name;
    }
}
