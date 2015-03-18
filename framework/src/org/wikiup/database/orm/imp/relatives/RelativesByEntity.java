package org.wikiup.database.orm.imp.relatives;

import org.wikiup.core.inf.Attribute;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.database.orm.inf.Relatives;

public class RelativesByEntity implements Relatives.OneToOne {
    private EntityModel entity;
    
    public RelativesByEntity(EntityModel entity) {
        this.entity = entity;
    }
    
    @Override
    public Iterable<Attribute> getProperties() {
        return entity.getAttributes();
    }

    @Override
    public Attribute get(String name) {
        return entity.get(name);
    }
}
