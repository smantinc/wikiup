package org.wikiup.modules.worms.imp.relatives;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.database.orm.util.SQLUtil;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.imp.builder.RawSQLBuilder;

public class RawRelatives extends ResultSetRelatives {
    public void init(Document desc, WormsEntity origin, Dictionary<?> dictionary) {
        init(desc, origin);
        RawSQLBuilder builder = new RawSQLBuilder(desc, origin, dictionary);
        setResultSet(SQLUtil.sqlQuery(origin.getConnection(), builder.buildSelectSQL()));
    }
}
