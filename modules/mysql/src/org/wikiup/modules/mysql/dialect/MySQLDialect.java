package org.wikiup.modules.mysql.dialect;

import org.wikiup.database.orm.FieldMetadata;
import org.wikiup.database.orm.SQLDialectBase;

public class MySQLDialect extends SQLDialectBase {
    @Override
    public String quote(String name, QuoteType quoteType) {
        return quoteType != QuoteType.string ? '`' + name + '`' : super.quote(name, quoteType);
    }

    @Override
    public String getDefinition(FieldMetadata field) {
        if(field.isIdentity())
            return field.getFieldName() + " INTEGER PRIMARY KEY AUTO_INCREMENT";
        return super.getDefinition(field);
    }

    public String getLastIdentitySQL() {
        return "SELECT LAST_INSERT_ID()";
    }
}