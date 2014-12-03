package org.wikiup.modules.worms.imp.builder;

import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.database.orm.util.SQLStatement;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.imp.FieldProperty;

public abstract class BaseSQLBuilder extends AbstractSQLBuilder {
    public BaseSQLBuilder(Document data, WormsEntity origin, Getter<?> getter) {
        super(data, origin, getter);
    }

    public SQLStatement buildSelectSQL() {
        SQLStatement stmt = new SQLStatement();
        buildDefaultSelectSQL(stmt);
        appendSQLSuffix(stmt);
        return stmt;
    }

    protected String getFieldName(Document node) {
        String name = Documents.getAttributeValue(node, "name", null);
        String fieldName = name != null ? ((FieldProperty) entity.getProperty(name)).getFieldName() : Documents.getAttributeValue(node, "field-name", null);
        Assert.notNull(fieldName, AttributeException.class, node, "name");
        return fieldName;
    }
}
