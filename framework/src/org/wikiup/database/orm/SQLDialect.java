package org.wikiup.database.orm;

import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.Filter;
import org.wikiup.database.orm.inf.DialectInterpretActionInf;
import org.wikiup.database.orm.inf.SQLDialectInf;

public abstract class SQLDialect implements SQLDialectInf {
    private DefaultSQLDialect dialect = Wikiup.getModel(DefaultSQLDialect.class);

    public DialectInterpretActionInf getInterpretor(String name) {
        return dialect.getInterpretor(name);
    }

    public Filter<Object, Object> getFieldFilter(String name) {
        return dialect.getFieldFilter(name);
    }

    public String getLocation(String catalog, String schema, String table) {
        return dialect.getLocation(catalog, schema, table, this);
    }

    public String getDefinition(FieldMetadata field) {
        return dialect.getDefinition(field);
    }

    public String quote(String name, QuoteType quoteType) {
        return '"' + name + '"';
    }
}
