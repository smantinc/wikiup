package org.wikiup.modules.worms.imp.relatives;

import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.inf.DataSourceInf;
import org.wikiup.database.orm.util.SQLUtil;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.WormsEntityManager;
import org.wikiup.modules.worms.imp.builder.RelativeSQLBuilder;
import org.wikiup.modules.worms.imp.iterator.EntityDocumentIterator;

import java.sql.Connection;
import java.util.Iterator;

public class OneToManyRelatives extends EntityRelatives {

    public void init(Document desc, WormsEntity origin, Getter<?> parameters) {
        Connection conn = origin.getConnection();
        DataSourceInf ds = Wikiup.getModel(DataSourceInf.class);
        WormsEntity entity = (WormsEntity) WormsEntityManager.getInstance().getEntityInterface(StringUtil.evaluateEL(Documents.getAttributeValue(desc, "entity-name", origin.getName()), parameters), ds, conn);
        RelativeSQLBuilder builder = new RelativeSQLBuilder(desc, entity, parameters);
        entity.setResultSet(SQLUtil.sqlQuery(conn, builder.buildSelectSQL()), true, false);
        setEntity(entity);
    }

    @Override
    public Iterable<Document> getChildren() {
        return new Iterable<Document>() {
            public Iterator<Document> iterator() {
                return new EntityDocumentIterator(getEntity());
            }
        };
    }

    @Override
    public Iterable<Document> getChildren(String name) {
        return getChildren();
    }
}
