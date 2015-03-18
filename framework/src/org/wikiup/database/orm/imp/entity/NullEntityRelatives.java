package org.wikiup.database.orm.imp.entity;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;
import org.wikiup.database.orm.inf.Relatives;

public class NullEntityRelatives implements Relatives {
    @Override
    public Iterable<Attribute> getProperties() {
        return Null.getInstance();
    }

    @Override
    public Attribute get(String name) {
        return null;
    }
}
