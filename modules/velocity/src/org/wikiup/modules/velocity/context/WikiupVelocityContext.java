package org.wikiup.modules.velocity.context;

import org.apache.velocity.context.Context;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.util.ContextUtil;
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
        BeanFactory mc = context.getModelContainer(s, null);
        if(mc != null) {
            Document doc = mc.query(Document.class);
            if(doc != null)
                return new org.wikiup.modules.velocity.context.WikiupDocumentVelocityContext(doc);
            Getter<?> getter = mc.query(Getter.class);
            if(getter != null)
                return new org.wikiup.modules.velocity.context.WikiupAccessorVelocityContext(context, getter);
        }
        Object obj = context.getAttribute(s);
        return Util.toVelocityObject(s, obj != null ? obj : ContextUtil.get(context, s, null));
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
