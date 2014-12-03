package org.wikiup.modules.oracle.dialect;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.database.orm.inf.DialectInterpretActionInf;
import org.wikiup.database.orm.util.SQLStatement;

public class LimitPhraseInterpretAction implements DialectInterpretActionInf {
    public static final String LIMIT_SUFFIX = " ) WHERE rownum <= ";

    public void doAction(SQLStatement stmt, Document param) {
        String limit = Documents.getAttributeValue(param, "limit");
        stmt.appendWrapper("SELECT * FROM ( ", LIMIT_SUFFIX + limit);
    }
}