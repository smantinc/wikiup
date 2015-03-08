package org.wikiup.modules.velocity.context;

import org.apache.velocity.context.Context;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.servlet.ServletProcessorContext;

public class WikiupVelocityContext implements Context {
    private ServletProcessorContext context;

    public WikiupVelocityContext(ServletProcessorContext context) {
        this.context = context;
    }

    public boolean containsKey(Object object) {
        return context.get(object.toString()) != null;
    }

    public Object get(String s) {
        BeanContainer mc = context.getModelContainer(s, null);
        if(mc != null) {
            Document doc = mc.query(Document.class);
            if(doc != null)
                return new org.wikiup.modules.velocity.context.WikiupDocumentVelocityContext(doc);
            Dictionary<?> dictionary = mc.query(Dictionary.class);
            if(dictionary != null)
                return new org.wikiup.modules.velocity.context.WikiupAccessorVelocityContext(context, dictionary);
        }
        Object obj = context.getAttribute(s);
        return Util.toVelocityObject(s, obj != null ? obj : Dictionaries.get(context, s, null));
    }

    public Object[] getKeys() {
        return null;
    }

    public Object put(String s, Object obj) {
        context.setAttribute(s, obj);
        return obj;
    }

    public Object remove(Object obj) {
        return obj;
    }
}
