package org.wikiup.modules.oracle.dialect;

import org.wikiup.database.orm.SQLDialectBase;
import org.wikiup.database.orm.inf.DialectInterpretAction;

public class OracleDialect extends SQLDialectBase {
    @Override
    public DialectInterpretAction getInterpretor(String name) {
        if(name.equals("limit"))
            return new LimitPhraseInterpretAction();
        if(name.equals("offset"))
            return new OffsetPhraseInterpretAction();
        return super.getInterpretor(name);
    }

    public String getLastIdentitySQL() {
        return null;
    }
}