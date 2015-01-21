package org.wikiup.modules.hsqldb.dialect;

import org.wikiup.database.orm.FieldMetadata;
import org.wikiup.database.orm.SQLDialectBase;
import org.wikiup.database.orm.inf.DialectInterpretActionInf;

public class HSQLDialect extends SQLDialectBase {
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
            return field.getFieldName() + " IDENTITY";
        return super.getDefinition(field);
    }

    public String getLastIdentitySQL() {
        return "CALL IDENTITY()";
    }
}
