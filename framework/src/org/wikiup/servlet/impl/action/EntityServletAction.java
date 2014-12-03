package org.wikiup.servlet.impl.action;

import org.wikiup.core.impl.document.DocumentImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.database.util.EntityPath;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class EntityServletAction implements ServletAction {

    public void doAction(ServletProcessorContext context, Document node) {
        EntityPath ePath = context.getEntityPath(node, context);
        Document doc = ePath.getDocument();
        Assert.notNull(doc);

        Document buf = new DocumentImpl("root");
        Documents.merge(buf, doc);

        copyToResponse(context, buf, Documents.getAttributeValue(node, "node-name", null));

        String var = context.getContextAttribute(node, "var", null);
        if(var != null)
            context.setAttribute(var, ePath.getEntity());
    }

    private void copyToResponse(ServletProcessorContext context, Document doc, String nodeName) {
        Document response = context.getResponseXML();
        int cc = nodeName != null ? 2 : childCount(doc);
        switch(cc) {
            case 0:
                Documents.mergeAttribute(response, doc);
                break;
            case 1:
                Documents.mergeAttribute(response, doc.getChildren().iterator().next());
                break;
            default:
                Documents.mergeChildren(response, doc.getChildren(), nodeName, true);
                break;
        }
    }

    private int childCount(Document doc) {
        int i = 0;
        for(Document node : doc.getChildren())
            i++;
        return i;
    }
}