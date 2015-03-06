package org.wikiup.modules.worms.imp.builder;

import org.wikiup.core.exception.WikiupRuntimeException;
import org.wikiup.core.impl.document.DocumentWithGetter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.orm.inf.DialectInterpretAction;
import org.wikiup.database.orm.inf.SQLDialect;
import org.wikiup.database.orm.util.SQLStatement;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.imp.FieldProperty;
import org.wikiup.modules.worms.imp.component.Property;
import org.wikiup.modules.worms.inf.SQLBuilderInf;

public abstract class AbstractSQLBuilder implements SQLBuilderInf {
    protected WormsEntity entity;
    protected Document document;
    protected SQLDialect dialect;

    private Dictionary<?> parameters;

    protected abstract void buildWhereClause(SQLStatement stmt);

    protected abstract String getFieldName(Document node);

    public AbstractSQLBuilder(Document document, WormsEntity entity, Dictionary<?> dictionary) {
        this.document = document;
        this.entity = entity;
        this.parameters = dictionary;
        dialect = entity.getDataSource().getDatabaseDriver().getDialect();
    }

    protected String getLocationName() {
        String table = Documents.getAttributeValue(document, "table", null);
        String schema = Documents.getAttributeValue(document, "schema", null);
        String catalog = Documents.getAttributeValue(document, "catalog", null);
        return getLocationName(catalog, schema, table);
    }

    protected String getLocationName(String catalog, String schema, String table) {
        return StringUtil.evaluateEL(dialect.getLocation(catalog, schema != null ? schema : entity.getSchema(), table != null ? table : entity.getTable()), parameters);
    }

    protected void connectPhrase(StringBuilder clause, String phrase, String connector) {
        clause.append(clause.length() == 0 ? phrase : connector + phrase);
    }

    protected String buildSelectClause() {
        StringBuilder clause = new StringBuilder();
        entity.getKeySet().clear();
        for(Property attr : entity.getProperties()) {
            FieldProperty field = getFieldProperty(attr);
            if(field != null && !field.isCriteria())
                buildSelectPhrase(clause, field);
        }
        return clause.toString();
    }

    protected FieldProperty getFieldProperty(Object item) {
        return item instanceof FieldProperty ? (FieldProperty) item : null;
    }

    protected void buildSelectPhrase(StringBuilder clause, FieldProperty field) {
        String name = dialect.quote(field.getName(), SQLDialect.QuoteType.string);
        String fieldSQL = field.getFieldSQL();
        connectPhrase(clause, (fieldSQL != null ? StringUtil.evaluateEL(fieldSQL, parameters) : dialect.quote(field.getFieldName(), SQLDialect.QuoteType.field)) + " " + name, ",");
    }

    protected void appendSQLSuffix(SQLStatement sql) {
        appendSQLAppendix(sql, document, "group-by", parameters);
        appendSQLAppendix(sql, document, "order-by", parameters);
        appendSQLAppendix(sql, document, "limit", parameters);
        appendSQLAppendix(sql, document, "offset", parameters);
    }

    protected void appendWhereClause(String clause, SQLStatement sql) {
        if(clause.length() > 0)
            sql.append(" WHERE " + clause);
    }

    protected void buildDefaultSelectSQL(SQLStatement sql) {
        sql.append("SELECT ");
        sql.append(buildSelectClause());
        sql.append(" FROM ");
        sql.append(getLocationName());
        buildWhereClause(sql);
    }

    protected void appendSQLAppendix(SQLStatement stmt, Document node, String name, Dictionary<?> dictionary) {
        if(node.getAttribute(name) != null) {
            DialectInterpretAction interpretor = dialect.getInterpretor(name);
            Assert.notNull(interpretor, WikiupRuntimeException.class, name);
            interpretor.doAction(stmt, new DocumentWithGetter(node, dictionary));
        }
    }

    public SQLStatement buildDeleteSQL() {
        SQLStatement stmt = new SQLStatement("DELETE FROM " + getLocationName());
        buildWhereClause(stmt);
        return stmt;
    }

    public SQLStatement buildInsertSQL() {
        SQLStatement stmt = new SQLStatement("INSERT INTO " + getLocationName());
        StringBuilder fieldsBuffer = new StringBuilder();
        StringBuilder valuesBuffer = new StringBuilder();
        for(Property property : entity.getProperties()) {
            FieldProperty field = getFieldProperty(property);
            if(field != null) {
                Object value = field.getFieldValue();
                if(value == null)
                    value = field.getDefaultValue();
                if(value != null) {
                    connectPhrase(fieldsBuffer, dialect.quote(field.getFieldName(), SQLDialect.QuoteType.field), ",");
                    connectPhrase(valuesBuffer, "?(" + field.getName() + ")", ",");
                    stmt.addParameter(field.getName(), value);
                }
            }
        }
        stmt.append(" (" + fieldsBuffer.toString() + ")");
        stmt.append(" VALUES(" + valuesBuffer.toString() + ")");
        return stmt;
    }

    protected boolean isKeyProperty(Property property) {
        return entity.getKeySet().contains(property.getName());
    }

    public SQLStatement buildUpdateSQL() {
        SQLStatement stmt = new SQLStatement("UPDATE " + getLocationName() + " SET ");
        StringBuilder buf = new StringBuilder();
        for(Property property : entity.getProperties()) {
            FieldProperty field = getFieldProperty(property);
            if(field != null && field.isDirty() && !isKeyProperty(field)) {
                Object value = field.getFieldValue();
                connectPhrase(buf, dialect.quote(field.getFieldName(), SQLDialect.QuoteType.field), ",");
                buf.append("=?(");
                buf.append(field.getName());
                buf.append(')');
                stmt.addParameter(field.getName(), value);
                field.setDirty(false);
            }
        }
        stmt.append(buf.toString());
        buildWhereClause(stmt);
        return stmt;
    }

    protected Dictionary<?> getParameters() {
        return parameters;
    }
}
