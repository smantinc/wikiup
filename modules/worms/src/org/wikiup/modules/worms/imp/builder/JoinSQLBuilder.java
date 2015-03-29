package org.wikiup.modules.worms.imp.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wikiup.core.Constants;
import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.orm.util.SQLStatement;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.WormsEntityManager;
import org.wikiup.modules.worms.imp.FieldProperty;
import org.wikiup.modules.worms.imp.component.Property;

public class JoinSQLBuilder extends AbstractSQLBuilder {
    private Document configure;
    private Map<String, WormsEntity> entityCache = new HashMap<String, WormsEntity>();
    private Map<String, String> aliasNames = new HashMap<String, String>();

    private Map<String, SQLJoinEntity> joinedEntities = new LinkedHashMap<String, SQLJoinEntity>();
    private StringBuilder whereClause = new StringBuilder();
    private String fieldConnector;

    public JoinSQLBuilder(Document configure, WormsEntity origin, Dictionary<?> dictionary) {
        super(configure, origin, dictionary);
        this.configure = configure;
        this.fieldConnector = Documents.getAttributeValue(configure, "field-connector", ".");
    }

    public SQLStatement buildSelectSQL() {
        SQLStatement stmt = new SQLStatement();
        prepareEntityObjectNames(configure);
        List<SQLJoinCriteria> condition = buildConditionClause(configure);
        stmt.append("SELECT ");
        stmt.append(stmt.translate(getParameters(), buildSelectClause(configure)));
        stmt.append(" FROM ");
        stmt.append(stmt.translate(getParameters(), bulidJoinClause(condition)));

        if(whereClause.length() > 0) {
            stmt.append(" WHERE ");
            stmt.append(stmt.translate(getParameters(), whereClause.toString()));
        }

        appendSQLSuffix(stmt);
        return stmt;
    }

    public WormsEntity getEntityByAlias(String name) {
        String entityName = aliasNames.get(name);
        return entityName != null ? entityCache.get(entityName) : null;
    }

    private String bulidJoinClause(List<SQLJoinCriteria> condition) {
        StringBuilder joinClause = new StringBuilder();
        Set<String> tables = new HashSet<String>();
        Assert.isTrue(joinedEntities.size() > 1, IllegalStateException.class);
        for(String objectName : joinedEntities.keySet()) {
            SQLJoinEntity joinEntity = joinedEntities.get(objectName);
            String tableName = getFieldLocationName(objectName, true);
            String phrase = buildTableSetCondition(tables, objectName, condition);
            appendDelimiter(joinClause, " " + (joinEntity.joinType != null ? joinEntity.joinType : "INNER") + " JOIN ");
            joinClause.append(tableName);
            if(phrase != null) {
                joinClause.append(" ON (");
                joinClause.append(phrase);
                joinClause.append(")");
            }
        }
        return joinClause.toString();
    }

    private String buildTableSetCondition(Set<String> objects, String objectName, List<SQLJoinCriteria> condition) {
        StringBuilder buf = new StringBuilder();
        objects.add(getEntityName(objectName));
        if(objects.size() > 1) {
            Iterator<SQLJoinCriteria> iterator = condition.iterator();
            while(iterator.hasNext()) {
                SQLJoinCriteria item = iterator.next();
                if(objects.containsAll(item.objects)) {
                    connectPhrase(buf, item.phrase.toString(), " AND ");
                    iterator.remove();
                }
            }
        }
        return buf.length() == 0 ? null : buf.toString();
    }

    private void appendDelimiter(StringBuilder buffer, String delimiter) {
        if(buffer.length() != 0)
            buffer.append(delimiter);
    }

    private List<SQLJoinCriteria> buildConditionClause(Document desc) {
        List<SQLJoinCriteria> list = new ArrayList<SQLJoinCriteria>();
        for(Document doc : desc.getChildren("criteria")) {
            SQLJoinCriteria condition = new SQLJoinCriteria();
            buildConditionPhrase(condition.phrase, condition.objects, doc, " AND ");
            if(condition.phrase.length() > 0)
                list.add(condition);
        }
        for(Document doc : desc.getChildren("criterias")) {
            SQLJoinCriteria condition = new SQLJoinCriteria();
            buildComboConditionPhrase(condition, doc);
            list.add(condition);
        }
        return list;
    }

    private void buildComboConditionPhrase(SQLJoinCriteria condition, Document node) {
        StringBuilder buffer = new StringBuilder();
        appendDelimiter(condition.phrase, " AND ");
        condition.phrase.append('(');
        for(Document doc : node.getChildren())
            buildConditionPhrase(buffer, condition.objects, doc, " OR ");
        condition.phrase.append(buffer);
        condition.phrase.append(')');
    }

    private void buildConditionPhrase(StringBuilder clause, Set<String> tables, Document desc, String delimiter) {
        String rValue = Documents.getAttributeValue(desc, "r-value", null);
        String rCondition = Documents.getAttributeValue(desc, "condition", null);

        if(rValue != null || rCondition != null)
            clause = whereClause;

        appendDelimiter(clause, delimiter);
        buildFieldName(clause, getReferValue(desc, "l-name"), tables, getReferValue(desc, "l-property"));
        if(rCondition == null) {
            clause.append('=');
            if(rValue != null)
                clause.append(rValue);
            else
                buildFieldName(clause, getReferValue(desc, "r-name"), tables, getReferValue(desc, "r-property"));
        } else {
            if(rCondition.contains("#"))
                rCondition = StringUtil.evaluateEL(rCondition.replace('#', '$'), getParameters());
            clause.append(rCondition);
        }
    }

    private String getReferValue(Document node, String name) {
        String n = Documents.getAttributeValue(node, name, null);
        Assert.notNull(n, AttributeException.class, node, name);
        return getReference(n);
    }

    private String getReference(String name) {
        return StringUtil.evaluateEL(name, getParameters());
    }

    private void buildFieldName(StringBuilder buffer, String objectName, Set<String> objects, String propertyName) {
        WormsEntity entity = getEntity(objectName, true);
        FieldProperty field = Interfaces.cast(FieldProperty.class, entity.getProperty(propertyName));
        Assert.notNull(field);
        buildFieldName(buffer, objectName, getFieldLocationName(objectName, false), objects, field);
    }

    private void buildFieldName(StringBuilder buffer, String objName, String location, Set<String> objects, FieldProperty field) {
        if(objects != null)
            objects.add(getEntityName(objName));
        buffer.append(location);
        buffer.append('.');
        buffer.append(field.getFieldName());
    }

    private String getFieldLocationName(String objectName, boolean tableName) {
        if(isEntityName(getEntityName(objectName)) || tableName) {
            WormsEntity entity = getEntity(objectName, true);
            return getLocationName(null, entity.getSchema(), entity.getTable());
        }
        return getEntityName(objectName);
    }

    private boolean isEntityName(String objectName) {
        return !aliasNames.containsKey(objectName);
    }

    private void prepareEntityObjectNames(Document desc) {
        for(Document node : desc.getChildren("field")) {
            String name = Documents.getAttributeValue(node, Constants.Attributes.NAME);
            String entityName = getReference(Documents.getAttributeValue(node, Constants.Attributes.ENTITY_NAME, null));
            String propertyName = Documents.getAttributeValue(node, Constants.Attributes.PROPERTY_NAME);
            if(propertyName.equals("*"))
                aliasNames.put(name, entityName);
            updateJoinEntity(entityName, Documents.getAttributeValue(node, "join-type", null));
        }
    }

    private String buildSelectClause(Document desc) {
        StringBuilder clause = new StringBuilder();
        for(Document node : desc.getChildren("field"))
            buildSelectPhrase(clause, node);
        return clause.toString();
    }

    private void buildSelectPhrase(StringBuilder clause, Document desc) {
        String name = Documents.getAttributeValue(desc, Constants.Attributes.NAME);
        String entityName = getReference(Documents.getAttributeValue(desc, Constants.Attributes.ENTITY_NAME, null));
        String propertyName = Documents.getAttributeValue(desc, Constants.Attributes.PROPERTY_NAME);
        if(propertyName.equals("*")) {
            aliasNames.put(name, entityName);
            WormsEntity entity = getEntity(name, false);
            for(Property property : entity.getProperties()) {
                FieldProperty field = getFieldProperty(property);
                if(field != null) {
                    appendDelimiter(clause, ",");
                    buildFieldName(clause, name, getFieldLocationName(name, false), null, field);
                    clause.append(" '").append(name).append(fieldConnector).append(field.getName()).append('\'');
                }
            }
        } else {
            appendDelimiter(clause, ",");
            if(ValueUtil.toBoolean(Documents.getAttributeValue(desc, "distinct"), false))
                clause.append("DISTINCT ");
            if(entityName != null)
                buildFieldName(clause, entityName, null, propertyName);
            else
                clause.append(Documents.getAttributeValue(desc, "field-sql"));
            clause.append(' ');
            clause.append(name);
        }
    }

    private WormsEntity getEntity(String name, boolean addToList) {
        String entityName = getEntityAliasName(name);
        WormsEntity wormsEntity = entityCache.get(entityName);
        if(wormsEntity == null) {
            wormsEntity = (WormsEntity) WormsEntityManager.getInstance().getEntityInterface(entityName, entity.getDataSource(), entity.getConnection());
            entityCache.put(entityName, wormsEntity);
            if(addToList)
                updateJoinEntity(name, null);
        }
        return wormsEntity;
    }

    private String getEntityAliasName(String objectName) {
        return aliasNames.containsKey(objectName) ? aliasNames.get(objectName) : objectName;
    }

    private String getEntityName(String entityNameOrAlias) {
        return aliasNames.containsKey(entityNameOrAlias) ? aliasNames.get(entityNameOrAlias) : entityNameOrAlias;
    }

    private void updateJoinEntity(String entityNameOrAlias, String joinType) {
        String name = getEntityName(entityNameOrAlias);
        if(joinedEntities.containsKey(name))
            joinedEntities.get(name).update(isEntityName(entityNameOrAlias) ? null : entityNameOrAlias, joinType);
        else if(name != null)
            joinedEntities.put(name, new SQLJoinEntity(entityNameOrAlias, joinType));
    }

    @Override
    public SQLStatement buildUpdateSQL() {
        return null;
    }

    @Override
    public SQLStatement buildDeleteSQL() {
        return null;
    }

    @Override
    public SQLStatement buildInsertSQL() {
        return null;
    }

    protected void buildWhereClause(SQLStatement stmt) {
    }

    protected String getFieldName(Document node) {
        return null;
    }

    private static class SQLJoinCriteria {
        public Set<String> objects = new HashSet<String>();
        public StringBuilder phrase = new StringBuilder();
    }

    private static class SQLJoinEntity {
        private String aliasName;
        private String joinType;

        public SQLJoinEntity(String aliasName, String joinType) {
            this.aliasName = aliasName;
            this.joinType = joinType;
        }

        public void update(String aliasName, String joinType) {
            this.aliasName = aliasName != null ? aliasName : this.aliasName;
            this.joinType = joinType != null ? joinType : this.joinType;
        }
    }
}