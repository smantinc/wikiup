package org.wikiup.modules.worms.imp.component;

import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.impl.attribute.AttributeImpl;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;

import java.util.LinkedHashMap;
import java.util.Map;

public class Component extends AttributeImpl implements Context<Attribute, String> {
    private Map<String, Property> properties = new LinkedHashMap<String, Property>();

    public Component() {
    }

    public void addProperty(String name, Property pv) {
        properties.put(name, pv);
    }

    public void delProperty(String name) {
        properties.remove(name);
    }

    public Attribute get(String name) {
        return getProperty(name);
    }

    public void set(String name, String value) {
        getProperty(name).setObject(value);
    }

    public Iterable<Property> getProperties() {
        return properties.values();
    }

    public Property getProperty(String name) {
        return Assert.notNull(getPropertyObject(name), AttributeException.class, getName(), name);
    }

    public Property getPropertyObject(String name) {
        return properties.get(name);
    }

    public Property addProperty(Document desc) {
        String name = Documents.getId(desc);
        String clazz = Documents.getAttributeValue(desc, "class", null);
        String defValue = Documents.getAttributeValue(desc, "def-value", null);
        Property p = null;
        try {
            p = clazz != null ? (Property) Class.forName(clazz).getConstructor(Component.class).newInstance(this) : new Property(this);
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        p.aware(desc);
        p.setName(name);
        if(defValue != null)
            p.setObject(defValue);
        return p;
    }
}
