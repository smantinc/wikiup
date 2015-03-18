package org.wikiup.modules.worms.imp.relatives;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.database.orm.util.SQLUtil;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.imp.builder.JoinSQLBuilder;


public class JoinRelatives extends ResultSetRelatives {
    private JoinSQLBuilder builder;

    public void init(Document desc, WormsEntity one, Dictionary<?> dictionary) {
        init(desc, one);
        builder = new JoinSQLBuilder(desc, one, dictionary);
        setResultSet(SQLUtil.sqlQuery(one.getConnection(), builder.buildSelectSQL()));
    }
}
