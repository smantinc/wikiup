package org.wikiup.modules.worms.imp.relatives;

import java.sql.Connection;
import java.util.Iterator;

import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.inf.DataSource;
import org.wikiup.database.orm.imp.relatives.RelativesByEntity;
import org.wikiup.database.orm.inf.Relatives;
import org.wikiup.database.orm.util.SQLUtil;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.WormsEntityManager;
import org.wikiup.modules.worms.imp.builder.RelativeSQLBuilder;

public class OneToManyRelatives extends EntityRelatives implements Relatives.OneToMany {

    public void init(Document desc, WormsEntity origin, Dictionary<?> parameters) {
        Connection conn = origin.getConnection();
        DataSource ds = Wikiup.getModel(DataSource.class);
        WormsEntity entity = (WormsEntity) WormsEntityManager.getInstance().getEntityInterface(StringUtil.evaluateEL(Documents.getAttributeValue(desc, "entity-name", origin.getName()), parameters), ds, conn);
        RelativeSQLBuilder builder = new RelativeSQLBuilder(desc, entity, parameters);
        entity.setResultSet(SQLUtil.sqlQuery(conn, builder.buildSelectSQL()), true, false);
        setEntity(entity);
    }

    @Override
    public Iterable<Attribute> getProperties() {
        return getAttributes();
    }

    @Override
    public Iterator<Relatives> iterator() {
        WormsEntity entity = getEntity();
        return new IteratorImpl(entity.iterator());
    }
    
    private static class IteratorImpl implements Iterator<Relatives> {
        private Iterator<WormsEntity> iterator;

        public IteratorImpl(Iterator<WormsEntity> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Relatives next() {
            return new RelativesByEntity(iterator.next());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
