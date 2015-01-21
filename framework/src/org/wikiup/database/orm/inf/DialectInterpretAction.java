package org.wikiup.database.orm.inf;

import org.wikiup.core.inf.Action;
import org.wikiup.core.inf.Document;
import org.wikiup.database.orm.util.SQLStatement;

public interface DialectInterpretAction extends Action<SQLStatement, Document> {
}
