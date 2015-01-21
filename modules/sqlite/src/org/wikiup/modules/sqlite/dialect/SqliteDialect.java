package org.wikiup.modules.sqlite.dialect;

import org.wikiup.database.orm.FieldMetadata;
import org.wikiup.database.orm.SQLDialectBase;

public class SqliteDialect extends SQLDialectBase {
    @Override
    public String getLocation(String catalog, String schema, String table) {
        return table;
    }

    @Override
    public String getDefinition(FieldMetadata field) {
        if(field.isIdentity())
            return field.getFieldName() + " INTEGER PRIMARY KEY";
        return super.getDefinition(field);
    }

    public String getLastIdentitySQL() {
        return "SELECT last_insert_rowid()";
    }
}
