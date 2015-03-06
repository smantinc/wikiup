package org.wikiup.modules.worms.imp.relatives;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Documents;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.database.orm.util.SQLUtil;
import org.wikiup.database.util.EntityPath;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.imp.builder.JoinSQLBuilder;


public class JoinRelatives extends ResultSetRelatives {
    private JoinSQLBuilder builder;

    public void init(Document desc, WormsEntity one, Dictionary<?> dictionary) {
        init(desc, one);
        builder = new JoinSQLBuilder(desc, one, dictionary);
        setResultSet(SQLUtil.sqlQuery(one.getConnection(), builder.buildSelectSQL()));
    }

    @Override
    public Document getChild(String name) {
        EntityPath ep = new EntityPath(name);
        String entityName = ep.getEntityName();
        EntityModel entity = entityName != null ? builder.getEntityByAlias(entityName) : null;
        Document child = null;
        if(entity != null) {
            ep.setEntity(entity, new JoinRelativesContext(this, entityName));
            child = ep.getDocument();
        }
        return child != null ? child : super.getChild(name);
    }

    private static class JoinRelativesContext implements Context<Object, Object> {
        private String entityName;
        private JoinRelatives joinRelatives;

        public JoinRelativesContext(JoinRelatives joinRelatives, String entityName) {
            this.joinRelatives = joinRelatives;
            this.entityName = entityName;
        }

        public Object get(String name) {
            return Documents.getAttributeValue(joinRelatives, entityName + '.' + name, null);
        }

        public void set(String name, Object obj) {
            Documents.setAttributeValue(joinRelatives, entityName + '.' + name, obj);
        }
    }
}
