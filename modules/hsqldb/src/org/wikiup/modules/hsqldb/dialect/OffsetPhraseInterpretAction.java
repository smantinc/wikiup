package org.wikiup.modules.hsqldb.dialect;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.database.orm.inf.DialectInterpretActionInf;
import org.wikiup.database.orm.util.SQLPhrase;
import org.wikiup.database.orm.util.SQLStatement;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OffsetPhraseInterpretAction implements DialectInterpretActionInf {
    public void doAction(SQLStatement stmt, Document param) {
        List<SQLPhrase> phrases = stmt.getPhrases();
        boolean hasWhere = stmt.getSQL().indexOf("WHERE") != -1;
        String offset = Documents.getAttributeValue(param, "offset");
        if(!hasWhere)
            for(SQLPhrase phrase : phrases) {
                hasWhere = phrase.getSQL().indexOf("WHERE") != -1;
                if(hasWhere)
                    break;
            }
        if(hasWhere)
            stmt.appendPhrase("OFFSET " + offset);
        else {
            Pattern pattern = Pattern.compile("LIMIT [^\\s]+");
            Matcher matcher = pattern.matcher(stmt.getSQL());
            if(matcher.find())
                stmt.getSQL().replace(matcher.start() + 6, matcher.end(), offset);
        }
    }
}
