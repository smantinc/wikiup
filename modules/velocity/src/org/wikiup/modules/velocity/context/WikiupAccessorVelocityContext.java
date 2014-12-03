package org.wikiup.modules.velocity.context;

import org.apache.velocity.context.Context;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.ServletProcessorContext;

public class WikiupAccessorVelocityContext implements Context {
    private Getter<?> getter;
    private ServletProcessorContext context;

    public WikiupAccessorVelocityContext(ServletProcessorContext context, Getter<?> getter) {
        this.context = context;
        this.getter = getter;
    }

    public boolean containsKey(Object object) {
        try {
            return getter.get(object.toString()) != null;
        } catch(Exception ex) {
            return false;
        }
    }

    public Object get(String s) {
        Object object = getter.get(s);
        context.awaredBy(object);
        return org.wikiup.modules.velocity.context.Util.toVelocityObject(s, object);
    }

    public Object[] getKeys() {
        return null;
    }

    public Object put(String s, Object value) {
        return Interfaces.set(getter, s, value);
    }

    public Object remove(Object obj) {
        return obj;
    }
}
