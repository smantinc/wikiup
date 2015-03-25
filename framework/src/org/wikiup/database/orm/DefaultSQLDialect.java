package org.wikiup.database.orm;

import org.wikiup.core.Wikiup;
import org.wikiup.framework.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Translator;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.orm.imp.dialect.ia.SQLPhraseInterpretAction;
import org.wikiup.database.orm.inf.DialectInterpretAction;
import org.wikiup.database.orm.inf.SQLDialect;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class DefaultSQLDialect extends WikiupDynamicSingleton<DefaultSQLDialect> implements DocumentAware {
    private Map<String, Translator<Object, Object>> filters;
    private Map<String, DialectInterpretAction> interpretors;

    private String defaultKey;
    private Document sqlTypes;

    @Override
    public void aware(Document desc) {
        Document doc = desc.getChild("field-types");
        if(doc != null) {
            for(Document node : doc.getChildren()) {
                Translator<Object, Object> filter = Wikiup.getInstance().getBean(Translator.class, node);
                Interfaces.initialize(filter, node);
                filters.put(Documents.getId(node).toLowerCase(), filter);
            }
            defaultKey = Documents.getAttributeValue(doc, "default", defaultKey);
        }
        doc = desc.getChild("interpretors");
        if(doc != null) {
            for(Document node : doc.getChildren()) {
                DialectInterpretAction interpretor = new SQLPhraseInterpretAction();
                Interfaces.initialize(interpretor, node);
                interpretors.put(Documents.getId(node).toLowerCase(), interpretor);
            }
        }
        sqlTypes = desc.getChild("sql-types");
    }

    public Translator<Object, Object> getFieldFilter(String name) {
        String n = name != null ? name.toLowerCase() : name;
        return filters.containsKey(n) ? filters.get(n) : filters.get(defaultKey);
    }

    public DialectInterpretAction getInterpretor(String name) {
        return interpretors.get(name);
    }

    public String getLocation(String catalog, String schema, String table, SQLDialect dialect) {
        StringBuilder buffer = new StringBuilder();
        if(!StringUtil.isEmpty(catalog))
            buffer.append(dialect.quote(catalog, SQLDialect.QuoteType.catalog));
        else if(!StringUtil.isEmpty(schema))
            buffer.append(dialect.quote(schema, SQLDialect.QuoteType.schema));
        if(buffer.length() > 0)
            StringUtil.connect(buffer, dialect.quote(table, SQLDialect.QuoteType.table), '.');
        else
            buffer.append(dialect.quote(table, SQLDialect.QuoteType.table));
        return buffer.toString();
    }

    public String getDefinition(FieldMetadata field) {
        return field.getFieldName() + " " + getDefinitionBySQLType(field.getFieldType());
    }

    public String getDefinitionBySQLType(int sqltype) {
        for(Document node : sqlTypes.getChildren()) {
            String[] types = Documents.getAttributeValue(node, "id").split("\\|");
            for(String type : types)
                if(ValueUtil.toInteger(type, Types.OTHER) == sqltype)
                    return Documents.getAttributeValue(node, "type");
        }
        return "VARCHAR(128)";
    }

    public void firstBuilt() {
        filters = new HashMap<String, Translator<Object, Object>>();
        interpretors = new HashMap<String, DialectInterpretAction>();
    }
}
