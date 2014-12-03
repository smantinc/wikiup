package org.wikiup.modules.worms.imp.relatives;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.database.orm.util.SQLUtil;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.imp.builder.RawSQLBuilder;

public class RawRelatives extends ResultSetRelatives {
    public void init(Document desc, WormsEntity origin, Getter<?> getter) {
        init(desc, origin);
        RawSQLBuilder builder = new RawSQLBuilder(desc, origin, getter);
        setResultSet(SQLUtil.sqlQuery(origin.getConnection(), builder.buildSelectSQL()));
    }
}
