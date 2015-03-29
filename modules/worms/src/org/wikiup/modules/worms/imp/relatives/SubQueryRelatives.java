package org.wikiup.modules.worms.imp.relatives;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.database.orm.util.SQLStatement;
import org.wikiup.database.orm.util.SQLUtil;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.imp.builder.JoinSQLBuilder;

public class SubQueryRelatives extends ResultSetRelatives {
    public void init(Document desc, WormsEntity one, Dictionary<?> dictionary) {
        init(desc, one);
        Document subQuery = desc.getChild("sub-query");
        JoinSQLBuilder builder = new JoinSQLBuilder(subQuery, one, dictionary);
        SQLStatement sqlStatement = builder.buildSelectSQL();
        StringBuilder sb = new StringBuilder();
        for(Document field : desc.getChildren("field")) {
            if(sb.length() > 0)
                sb.append(", ");
            sb.append(Documents.ensureAttributeValue(field, "field-sql"));
            sb.append(' ');
            sb.append(Documents.ensureAttributeValue(field, "name"));
        }
        sqlStatement.appendWrapper(String.format("SELECT %s FROM (", sb.toString()), ") x");
        setResultSet(SQLUtil.sqlQuery(one.getConnection(), sqlStatement));
    }
}
