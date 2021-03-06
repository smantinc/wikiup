package org.wikiup.database.orm.inf;

import org.wikiup.core.inf.Translator;
import org.wikiup.database.orm.FieldMetadata;

public interface SQLDialect {
    public enum QuoteType {field, table, schema, catalog, string}

    public DialectInterpretAction getInterpretor(String name);

    public Translator<Object, Object> getFieldFilter(String name);

    public String getLocation(String catalog, String schema, String table);

    public String getDefinition(FieldMetadata field);

    public String quote(String name, QuoteType quoteType);

    public String getLastIdentitySQL();
}
