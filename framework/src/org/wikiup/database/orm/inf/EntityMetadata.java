package org.wikiup.database.orm.inf;

import org.wikiup.database.orm.FieldMetadata;

public interface EntityMetadata {
    public Iterable<FieldMetadata> getProperties();

    public String getTable();

    public String getSchema();

    public String getCatalog();

    public String getName();
}
