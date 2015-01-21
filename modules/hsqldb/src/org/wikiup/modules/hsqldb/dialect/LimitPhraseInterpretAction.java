package org.wikiup.modules.hsqldb.dialect;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.database.orm.inf.DialectInterpretAction;
import org.wikiup.database.orm.util.SQLPhrase;
import org.wikiup.database.orm.util.SQLStatement;

import java.util.List;

public class LimitPhraseInterpretAction implements DialectInterpretAction {
    public void doAction(SQLStatement stmt, Document param) {
        List<SQLPhrase> phrases = stmt.getPhrases();
        boolean hasWhere = stmt.getSQL().indexOf("WHERE") != -1;
        String limit = Documents.getAttributeValue(param, "limit");
        if(!hasWhere)
            for(SQLPhrase phrase : phrases) {
                hasWhere = phrase.getSQL().indexOf("WHERE") != -1;
                if(hasWhere)
                    break;
            }
        if(hasWhere)
            stmt.appendPhrase("LIMIT " + limit);
        else {
            int idx = stmt.getSQL().indexOf("SELECT");
            stmt.getSQL().insert(idx + 6, " LIMIT 0 " + limit);
        }
    }
}
