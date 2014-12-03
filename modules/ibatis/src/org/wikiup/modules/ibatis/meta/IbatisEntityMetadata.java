package org.wikiup.modules.ibatis.meta;

import org.wikiup.database.orm.FieldMetadata;
import org.wikiup.database.orm.inf.EntityMetadata;

public class IbatisEntityMetadata implements EntityMetadata {
    private String name;

    public IbatisEntityMetadata(String id) {
        name = id;
    }

    public Iterable<FieldMetadata> getProperties() {
        return null;
    }

    public String getTable() {
        return null;
    }

    public String getSchema() {
        return null;
    }

    public String getCatalog() {
        return null;
    }

    public String getName() {
        return name;
    }
}
