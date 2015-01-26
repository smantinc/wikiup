package org.wikiup.modules.worms.imp;

import org.wikiup.core.Constants;
import org.wikiup.core.impl.attribute.AttributeImpl;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Translator;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.imp.component.Component;
import org.wikiup.modules.worms.imp.component.Property;
import org.wikiup.modules.worms.inf.PropertyActionInf;

import java.sql.ResultSet;

public class FieldProperty extends Property {
    private String fieldName;
    private String fieldType;
    private String fieldSQL;
    private boolean isPrimaryKey, isIndexKey, dirty;
    private Document fieldDescription;

    private Object fieldValue;

    public FieldProperty(Component owner, Document data) {
        super(owner);
        aware(data);
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isCriteria() {
        return fieldDescription.getName().contains("criteria");
    }

    public boolean isIndexKey() {
        return isIndexKey;
    }

    public String getFieldName() {
        return fieldName != null ? fieldName : getName();
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getFieldSQL() {
        return fieldSQL;
    }

    public ResultSet getRecord(boolean refresh) {
        return ((WormsEntity) getOwner()).getResultSet(refresh);
    }

    public String getDefaultValue() {
        return Documents.getAttributeValue(fieldDescription, "def-value", null);
    }

    @Override
    public void aware(Document desc) {
        fieldDescription = desc;
        fieldName = Documents.getAttributeValue(fieldDescription, "field-name", null);
        fieldSQL = Documents.getAttributeValue(fieldDescription, "field-sql", null);
        fieldType = Documents.getAttributeValue(fieldDescription, "field-type", null);
        isPrimaryKey = Documents.getAttributeBooleanValue(fieldDescription, "primary-key", false);
        isIndexKey = isPrimaryKey || Documents.getAttributeBooleanValue(fieldDescription, "index-key", false);
        super.aware(desc);
    }

    @Override
    protected Attribute setupPropertyValue(Document desc) {
        String name = Documents.getAttributeValue(desc, Constants.Attributes.NAME);
        String value = Documents.getAttributeValue(desc, Constants.Attributes.VALUE, null);
        setActionListener(GET_OBJECT_ACTION_NAME, new FieldPropertyGetListener());
        setActionListener(SET_OBJECT_ACTION_NAME, new FieldPropertySetListener());
        return isCriteria() ? new AttributeImpl(name, value) : new FieldPropertyValue(this, name, value);
    }

    public Object cast(Object obj) {
        Translator<Object, Object> filter = ((WormsEntity) getOwner()).getDataSource().getDatabaseDriver().getDialect().getFieldFilter(fieldType);
        return filter.translate(obj);
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    class FieldPropertyGetListener implements PropertyActionInf {
        public void doAction(Property sender, Attribute param) {
        }
    }

    class FieldPropertySetListener implements PropertyActionInf {
        public void doAction(Property sender, Attribute param) {
            FieldPropertyValue pv = param instanceof FieldPropertyValue ? (FieldPropertyValue) param : null;
            String value = pv != null ? pv.getValue() : (param != null ? ValueUtil.toString(param.getObject()) : null);
            fieldValue = value != null ? value : null;
            dirty = true;
        }
    }

}
