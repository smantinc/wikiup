package org.wikiup.servlet.impl.action;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.orm.Entity;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;
import org.wikiup.servlet.util.ActionUtil;

public class DeleteServletAction implements ServletAction {
    public void doAction(ServletProcessorContext context, Document node) {
        Entity entity = ActionUtil.getEntity(context, node);
        context.setContextProperties(node, entity);
        try {
            if(ValueUtil.toBoolean(Documents.getAttributeValue(node, "select", null), false))
                entity.select();
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        entity.delete();
    }
}
