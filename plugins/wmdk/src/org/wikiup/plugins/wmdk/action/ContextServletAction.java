package org.wikiup.plugins.wmdk.action;

import java.util.Iterator;

import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.exception.ServiceNotImplementException;
import org.wikiup.servlet.util.ProcessorContexts;

public class ContextServletAction {
    public void get(ServletProcessorContext context) {
        Document resp = context.getResponseXML();
        String name = context.getParameter("name");
        Documents.setAttributeValue(resp, "value", context.get(name));
    }

    public void contexts(ServletProcessorContext context) {
        String node = context.getParameter("node");
        int pos = node.indexOf('@');
        String uri = pos == -1 ? node : node.substring(0, pos);
        String objectName = pos == -1 ? null : node.substring(pos + 1);
        Document resp = context.getResponseXML();
        ServletProcessorContext ctx = getRequestedContext(context, uri);
        if(ctx != null)
            if(objectName == null) {
                Object globalContext = Wikiup.getInstance().get("wk.servlet.context");
                Document rScope = ctx.getRequestContextConf();
                Document sScope = ctx.getServletContextConf();
                Iterable<Object> iterable = Interfaces.getModel(globalContext, Iterable.class);
                if(sScope != null)
                    mergeContext(uri, sScope, resp);
                if(rScope != null)
                    mergeContext(uri, rScope, resp);
                if(iterable != null)
                    for(Object obj : iterable) {
                        String name = ValueUtil.toString(obj);
                        appendContextNode(resp, "context", uri + '@' + name, name, false);
                    }
            } else {
                ctx.doInit();
                Iterator<Object> iterator = getIterator(ctx, objectName);
                while(iterator != null && iterator.hasNext()) {
                    String name = ValueUtil.toString(iterator.next());
                    String p = StringUtil.connect(objectName, name, ':');
                    appendContextNode(resp, "item", uri + '@' + p, name, getIterator(ctx, p) == null);
                }
            }
    }

    private ServletProcessorContext getRequestedContext(ServletProcessorContext context, String uri) {
        ServletProcessorContext ctx = null;
        try {
            ctx = new ServletProcessorContext(context, uri);
        } catch(ServiceNotImplementException e) {
        }
        return ctx;
    }

    private void appendContextNode(Document resp, String nodeName, String id, String text, boolean isLeaf) {
        Document doc = resp.addChild(nodeName);
        Documents.setAttributeValue(doc, "text", text);
        Documents.setAttributeValue(doc, "id", id);
        Documents.setAttributeValue(doc, "leaf", isLeaf);
    }

    public void actions(ServletProcessorContext context) {
        String uri = context.getParameter("uri", "/");
        ServletProcessorContext ctx = getRequestedContext(context, uri);
        Document resp = context.getResponseXML();
        if(ctx != null) {
            Document rScope = ctx.getRequestContextConf().getChild("context-action");
            Document sScope = ctx.getServletContextConf().getChild("context-action");
            if(sScope != null)
                mergeContext(uri, sScope, resp);
            if(rScope != null) {
                if(!rScope.getChildren().iterator().hasNext())
                    mergeAction(rScope, resp.addChild("action"));
                else
                    mergeAction(rScope, resp);
                for(Document doc : resp.getChildren())
                    Documents.setChildValue(doc, "scope", "Request");
            }
        }
    }

    private void mergeAction(Document action, Document resp) {
        for(Attribute attr : action.getAttributes())
            Documents.setChildValue(resp, attr.getName(), attr.toString());
    }

    private Iterator<Object> getIterator(ServletProcessorContext ctx, String path) {
        try {
            BeanContainer mc = ProcessorContexts.getBeanContainer(ctx, path, Null.getInstance());
            return mc != null ? mc.query(Iterator.class) : null;
        } catch(Exception e) {
            return null;
        }
    }

    private void mergeContext(String uri, Document sScope, Document resp) {
        for(Document item : sScope.getChildren("context")) {
            Document node = resp.addChild("context");
            String name = Documents.getAttributeValue(item, "name");
            Documents.setAttributeValue(node, "text", name);
            Documents.setAttributeValue(node, "id", uri + '@' + name);
            Documents.setAttributeValue(node, "leaf", false);
        }
    }
}
