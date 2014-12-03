package org.wikiup.servlet.impl.action;

import org.wikiup.core.inf.Document;
import org.wikiup.database.orm.Entity;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;
import org.wikiup.servlet.util.ActionUtil;

public class InsertServletAction implements ServletAction {
    public void doAction(ServletProcessorContext context, Document node) {
        Entity entity = ActionUtil.getEntity(context, node);
        context.setContextProperties(node, entity);
        entity.insert();
    }
}
