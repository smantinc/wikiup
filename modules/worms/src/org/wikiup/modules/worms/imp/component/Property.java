package org.wikiup.modules.worms.imp.component;

import org.wikiup.core.impl.attribute.AttributeImpl;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.modules.worms.inf.PropertyActionInf;

import java.util.HashMap;
import java.util.Map;

public class Property implements Attribute, DocumentAware {
    private Component owner;
    private Attribute value;

    private Map<String, PropertyActionInf> events = new HashMap<String, PropertyActionInf>();

    public static final String GET_NAME_ACTION_NAME = "getname";
    public static final String SET_NAME_ACTION_NAME = "setname";
    public static final String GET_OBJECT_ACTION_NAME = "getobject";
    public static final String SET_OBJECT_ACTION_NAME = "setobject";

    public Property(Component owner) {
        this.owner = owner;
    }

    public Component getOwner() {
        return owner;
    }

    public void setActionListener(String name, PropertyActionInf l) {
        events.put(name, l);
    }

    public PropertyActionInf getActionListener(String name) {
        return events.get(name);
    }

    public void setAttribute(Attribute attr) {
        value = attr;
    }

    public void aware(Document desc) {
        value = setupPropertyValue(desc);
    }

    public String getName() {
        fireEvent(GET_NAME_ACTION_NAME);
        return value.getName();
    }

    public void setName(String name) {
        value.setName(name);
        fireEvent(SET_NAME_ACTION_NAME);
    }

    public Object getObject() {
        fireEvent(GET_OBJECT_ACTION_NAME);
        return value.getObject();
    }

    public void setObject(Object obj) {
        value.setObject(obj);
        fireEvent(SET_OBJECT_ACTION_NAME);
    }

    private void fireEvent(String name) {
        PropertyActionInf action = getActionListener(name);
        if(action != null)
            action.doAction(this, value);
    }

    @Override
    public String toString() {
        return ValueUtil.toString(this);
    }

    protected Attribute setupPropertyValue(Document desc) {
        return new AttributeImpl(Documents.getId(desc));
    }
}
