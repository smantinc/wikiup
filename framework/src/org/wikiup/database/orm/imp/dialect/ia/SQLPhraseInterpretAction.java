package org.wikiup.database.orm.imp.dialect.ia;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.orm.inf.DialectInterpretAction;
import org.wikiup.database.orm.util.SQLStatement;

public class SQLPhraseInterpretAction implements DialectInterpretAction, DocumentAware {
    private String phrase;

    public void doAction(SQLStatement stmt, final Document param) {
        stmt.appendPhrase(StringUtil.evaluateEL(phrase, new Dictionary<String>() {
            public String get(String name) {
                return Documents.getAttributeValue(param, name, null);
            }
        }));
    }

    public void aware(Document desc) {
        phrase = Documents.getAttributeValue(desc, "phrase");
    }
}