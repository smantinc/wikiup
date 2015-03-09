package org.wikiup.servlet.util;

import org.wikiup.core.Constants;
import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.orm.Entity;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.exception.InvalidRequestParameterException;
import org.wikiup.servlet.impl.action.ServletActionSupport;
import org.wikiup.servlet.impl.document.InterceptableDocument;
import org.wikiup.servlet.inf.ServletAction;

public class ActionUtil {
    public static Entity getEntity(ServletProcessorContext context, Document node) {
        Entity entity = getEntity(context, node, context.getContextAttribute(node, "entity-name", null));
        Assert.notNull(entity, "Entity not specified or not found by the 'entity-name' attribute.");
        return entity;
    }

    public static Entity getEntity(ServletProcessorContext context, Document node, String entityName) {
        String name = Documents.getDocumentValue(node, "as", null);
        Entity entity = entityName != null ? context.getEntity(entityName) : (name != null ? (Entity) context.getAttribute(name) : null);
        if(name != null && entityName != null)
            context.setAttribute(name, entity);
        return entity;
    }

    public static void doAction(ServletProcessorContext context, Document doc) {
        String entry = Documents.getAttributeValue(doc, "entry", null);
        Document action = entry != null ? doc.getChild(StringUtil.evaluateEL(entry, context)) : doc;
        Assert.notNull(action, InvalidRequestParameterException.class, "entry");
        doActionList(context, new InterceptableDocument(context, action));
    }

    private static void doActionList(ServletProcessorContext context, Document node) {
        doActionNode(context, node, getAction(node));
    }

    private static void doActionNode(ServletProcessorContext context, Document node, ServletAction action) {
        if(action != null)
            action.doAction(context, node);
        else {
            for(Document doc : node.getChildren())
                doAction(context, doc);
        }
    }

    private static ServletAction getAction(Document node) {
        if(node.getAttribute(Constants.Attributes.CLASS) == null)
            return null;
        ServletAction action = Wikiup.getInstance().getBean(ServletAction.class, node);
        if(action == null) {
            Object object = Wikiup.getInstance().getBean(Object.class, node);
            if(object != null)
                action = new ServletActionSupport(object);
        }
        return action;
    }
}
