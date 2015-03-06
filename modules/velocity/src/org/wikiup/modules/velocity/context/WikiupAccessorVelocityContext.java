package org.wikiup.modules.velocity.context;

import org.apache.velocity.context.Context;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.ServletProcessorContext;

public class WikiupAccessorVelocityContext implements Context {
    private Dictionary<?> dictionary;
    private ServletProcessorContext context;

    public WikiupAccessorVelocityContext(ServletProcessorContext context, Dictionary<?> dictionary) {
        this.context = context;
        this.dictionary = dictionary;
    }

    public boolean containsKey(Object object) {
        try {
            return dictionary.get(object.toString()) != null;
        } catch(Exception ex) {
            return false;
        }
    }

    public Object get(String s) {
        Object object = dictionary.get(s);
        context.awaredBy(object);
        return org.wikiup.modules.velocity.context.Util.toVelocityObject(s, object);
    }

    public Object[] getKeys() {
        return null;
    }

    public Object put(String s, Object value) {
        return Interfaces.set(dictionary, s, value);
    }

    public Object remove(Object obj) {
        return obj;
    }
}
