package org.wikiup.modules.worms.imp.builder;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.orm.util.SQLStatement;
import org.wikiup.modules.worms.WormsEntity;

public class RelativeSQLBuilder extends BaseSQLBuilder {
    public RelativeSQLBuilder(Document data, WormsEntity origin, Getter<?> getter) {
        super(data, origin, getter);
    }

    protected void buildWhereClause(SQLStatement stmt) {
        appendWhereClause(buildWhereClause(stmt, document, " AND "), stmt);
    }

    private String getCriteriaConnector(Document desc, String def) {
        String connector = Documents.getAttributeValue(desc, "connector", def);
        return connector != null ? " " + StringUtil.trim(connector).toLowerCase() + " " : " AND ";
    }

    private String buildWhereClause(SQLStatement stmt, Document desc, String connector) {
        StringBuffer clause = new StringBuffer();
        String c = getCriteriaConnector(desc, connector);
        for(Document node : desc.getChildren()) {
            String nodeName = node.getName();
            if(nodeName.equals("criterias"))
                connectPhrase(clause, "(" + buildWhereClause(stmt, node, Documents.getAttributeValue(node, "connector", " OR ")) + ")", c);
            else if(nodeName.equals("dynamic-criteria")) {
                String test = StringUtil.evaluateEL(Documents.getAttributeValue(node, "if", null), getParameters());
                if(StringUtil.test(test))
                    buildWhereCriteria(stmt, clause, c, node);
            } else
                buildWhereCriteria(stmt, clause, c, node);
        }
        return clause.toString();
    }

    private void buildWhereCriteria(SQLStatement stmt, StringBuffer clause, String connector, Document node) {
        String value = Documents.getAttributeValue(node, "foreign-key", null);
        if(value != null) {
            if((value = stmt.translate(getParameters(), value)) != null)
                connectPhrase(clause, getFieldName(node) + '=' + value, connector);
        } else if((value = Documents.getAttributeValue(node, "value", null)) != null)
            connectPhrase(clause, getFieldName(node) + '=' + stmt.translate(getParameters(), value), connector);
        else if((value = Documents.getAttributeValue(node, "condition", null)) != null) {
            boolean prepared = Documents.getAttributeBooleanValue(node, "prepared", true);
            connectPhrase(clause, getFieldName(node) + ' ' + (prepared ? stmt.translate(getParameters(), value) : StringUtil.evaluateEL(value, getParameters())), connector);
        }
    }
}
