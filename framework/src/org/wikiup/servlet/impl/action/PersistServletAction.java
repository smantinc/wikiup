package org.wikiup.servlet.impl.action;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.exception.InsufficientPrimaryKeys;
import org.wikiup.database.exception.RecordNotFoundException;
import org.wikiup.database.orm.Entity;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;
import org.wikiup.servlet.util.ActionUtil;

public class PersistServletAction implements ServletAction {
    public void doAction(ServletProcessorContext context, Document node) {
        Entity entity = ActionUtil.getEntity(context, node);
        try {
            String var = context.getContextAttribute(node, "var", null);
            if(var != null)
                context.setAttribute(var, entity);

            Document where = node.getChild("where");
            if(where != null) {
                context.setContextProperties(where, entity);
                entity.select();
            }
            if(ValueUtil.toBoolean(context.getContextAttribute(node, "update-if-not-exist", null), true)) {
                context.setContextProperties(node, entity);
                doUpdate(context, entity, node);
            }
        } catch(RecordNotFoundException ex) {
            doInsert(context, entity, node);
        } catch(InsufficientPrimaryKeys ex) {
            doInsert(context, entity, node);
        }
    }

    private void doInsert(ServletProcessorContext context, Entity entity, Document node) {
        Document insert = node.getChild("insert");
        context.setContextProperties(node, entity);
        if(insert != null)
            context.setContextProperties(insert, entity);
        entity.insert();
    }

    private void doUpdate(ServletProcessorContext context, Entity entity, Document node) {
        Document update = node.getChild("update");
        context.setContextProperties(node, entity);
        if(update != null)
            context.setContextProperties(update, entity);
        entity.update();
    }
}
