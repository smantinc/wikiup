package org.wikiup.servlet.impl.action;

import org.wikiup.core.impl.context.XPathContext;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class SetServletAction implements ServletAction {
    public void doAction(ServletProcessorContext context, Document node) {
        String target = Documents.getAttributeValue(node, "target", "context");
        if(target.equals("context"))
            context.setProperties(node, context);
        else if(target.equals("response-xml")) {
            Document doc = context.getResponseXML();
            context.setProperties(node, new XPathContext(doc));
        }
    }
}
