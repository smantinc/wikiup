package org.wikiup.modules.worms.imp;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ValueUtil;

import java.sql.ResultSet;

public class FieldPropertyValue implements Attribute {
    private String name;
    private Object value;
    private FieldProperty owner;

    public FieldPropertyValue(FieldProperty owner, String name, Object value) {
        this.name = name;
        this.value = value;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public Object getObject() {
        try {
            ResultSet record = owner.getRecord(value == null);
            value = record != null ? record.getObject(owner.getName()) : value;
            return value;
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setObject(Object obj) {
        value = owner.cast(obj);
    }

    public String getValue() {
        return ValueUtil.toString(value);
    }
}
