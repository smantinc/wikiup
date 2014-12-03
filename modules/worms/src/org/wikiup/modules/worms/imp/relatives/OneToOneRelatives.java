package org.wikiup.modules.worms.imp.relatives;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.orm.util.SQLUtil;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.WormsEntityManager;
import org.wikiup.modules.worms.imp.builder.RelativeSQLBuilder;

import java.sql.Connection;

public class OneToOneRelatives extends EntityRelatives {
    public void init(Document desc, WormsEntity origin, Getter<?> parameters) {
        Connection conn = origin.getConnection();
        String entityName = StringUtil.evaluateEL(Documents.getAttributeValue(desc, "entity-name", origin.getName()), parameters);
        WormsEntity entity = (WormsEntity) WormsEntityManager.getInstance().getEntityInterface(entityName, origin.getDataSource(), conn);
        RelativeSQLBuilder builder = new RelativeSQLBuilder(desc, entity, parameters);
        entity.setResultSet(SQLUtil.sqlQuery(conn, builder.buildSelectSQL()), true, true);
        setEntity(entity);
    }
}
