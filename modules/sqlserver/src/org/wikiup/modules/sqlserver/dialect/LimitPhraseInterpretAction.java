package org.wikiup.modules.sqlserver.dialect;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.database.orm.inf.DialectInterpretAction;
import org.wikiup.database.orm.util.SQLStatement;

public class LimitPhraseInterpretAction implements DialectInterpretAction {
    public void doAction(SQLStatement stmt, Document param) {
        int idx = stmt.getSQL().indexOf("SELECT");
        String limit = Documents.getAttributeValue(param, "limit");
        stmt.getSQL().insert(idx + 6, " TOP " + limit);
    }
}