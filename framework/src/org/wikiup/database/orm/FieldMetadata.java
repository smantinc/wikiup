package org.wikiup.database.orm;

import org.wikiup.core.util.StringUtil;

public class FieldMetadata {
    private int fieldType;
    private String fieldName;
    private boolean isPrimaryKey;
    private boolean isIdentity;

    public boolean isIdentity() {
        return isIdentity;
    }

    public void setIdentity(boolean identity) {
        isIdentity = identity;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public String getName() {
        return StringUtil.getCamelName(fieldName, '_');
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }
}