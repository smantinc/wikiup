package org.wikiup.modules.worms.imp.builder;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Assert;
import org.wikiup.database.exception.InsufficientPrimaryKeys;
import org.wikiup.database.orm.util.SQLStatement;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.imp.FieldProperty;
import org.wikiup.modules.worms.imp.component.Property;

public class EntitySQLBuilder extends BaseSQLBuilder {
    public EntitySQLBuilder(Document desc, WormsEntity origin, Dictionary<?> accessor) {
        super(desc, origin, accessor);
    }

    protected void buildWhereClause(SQLStatement stmt) {
        StringBuilder clause = new StringBuilder();
        for(Property property : entity.getProperties()) {
            FieldProperty field = getFieldProperty(property);
            if(field != null && (field.isDirty() || isKeyProperty(field))) {
                Object obj = field.getFieldValue();
                if(obj != null)
                    connectPhrase(clause, field.getFieldName() + "=?(" + field.getName() + ")", " AND ");
                stmt.addParameter(field.getName(), obj);
                entity.getKeySet().add(field.getName());
            }
        }
        if(clause.length() > 0)
            appendWhereClause(clause.toString(), stmt);
        else
            buildWhereClauseByPK(stmt);
    }

    private void buildWhereClauseByPK(SQLStatement stmt) {
        StringBuilder clause = new StringBuilder();
        for(Property property : entity.getProperties()) {
            FieldProperty field = getFieldProperty(property);
            if(field != null && field.isPrimaryKey()) {
                Object obj = field.getFieldValue();
                Assert.notNull(obj, InsufficientPrimaryKeys.class, entity.getName(), field.getName());
                connectPhrase(clause, field.getFieldName() + "=?(" + field.getName() + ")", " AND ");
                stmt.addParameter(field.getName(), obj);
                entity.getKeySet().add(field.getName());
            }
        }
        appendWhereClause(clause.toString(), stmt);
    }
}
