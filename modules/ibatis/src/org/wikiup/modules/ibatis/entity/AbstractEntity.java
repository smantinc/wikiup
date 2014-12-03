package org.wikiup.modules.ibatis.entity;


import org.wikiup.core.inf.Getter;
import org.wikiup.database.orm.EntityRelatives;
import org.wikiup.database.orm.inf.EntityModel;

public abstract class AbstractEntity implements EntityModel {
    private String name;

    public AbstractEntity(String name) {
        this.name = name;
    }

    public void bind(Object object) {
    }

    public void delete() {
    }

    public EntityRelatives getRelatives(String name, Getter<String> getter) {
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
