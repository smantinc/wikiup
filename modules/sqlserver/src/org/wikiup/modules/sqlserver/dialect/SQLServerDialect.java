package org.wikiup.modules.sqlserver.dialect;

import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.orm.FieldMetadata;
import org.wikiup.database.orm.SQLDialectBase;
import org.wikiup.database.orm.inf.DialectInterpretActionInf;

public class SQLServerDialect extends SQLDialectBase {
    @Override
    public DialectInterpretActionInf getInterpretor(String name) {
        if(name.equals("limit"))
            return new LimitPhraseInterpretAction();
        if(name.equals("offset"))
            return new OffsetPhraseInterpretAction();
        return super.getInterpretor(name);
    }

    @Override
    public String getDefinition(FieldMetadata field) {
        if(field.isIdentity())
            return field.getFieldName() + " INTEGER IDENTITY(1, 1) PRIMARY KEY";
        return super.getDefinition(field);
    }

    @Override
    public String getLocation(String catalog, String schema, String table) {
        StringBuilder buf = new StringBuilder();
        buf.append(ValueUtil.toString(catalog != null ? catalog : schema));
        if(buf.length() > 0)
            buf.append('.');
        buf.append("dbo.");
        buf.append(table);
        return buf.toString();
    }

    public String getLastIdentitySQL() {
        return "SELECT LAST_INSERT_ID()";
    }

    @Override
    public String quote(String name, QuoteType quoteType) {
        return quoteType != QuoteType.string ? '[' + name + ']' : super.quote(name, quoteType);
    }
}
