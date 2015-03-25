package org.wikiup.servlet.impl.eh;

import org.wikiup.core.Wikiup;
import org.wikiup.framework.bean.WikiupConfigure;
import org.wikiup.core.exception.WikiupRuntimeException;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ExceptionHandler;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class ServletExceptionHandler implements ExceptionHandler, ServletProcessorContextAware {
    private Document servletConf;
    private Document requestConf;
    private ServletProcessorContext context;

    public ServletExceptionHandler(Document servletConf, Document requestConf) {
        this.servletConf = servletConf;
        this.requestConf = requestConf;
    }

    public boolean handle(Exception exp) {
        if(!(exp instanceof WikiupRuntimeException)) {
            Document node = exp != null ? getHandlerNode(exp) : null;
            if(node != null) {
                ExceptionHandler handler = Wikiup.getInstance().getBean(ExceptionHandler.class, node);
                Interfaces.initialize(handler, node);
                context.awaredBy(handler);
                return handler.handle(exp);
            }
        }
        return false;
    }

    private Document getHandlerNode(Exception exp) {
        String name = exp.getClass().getName();
        Document ret;
        if((ret = getHandlerNode(WikiupConfigure.getInstance().lookup("wk/exception-handler"), name)) != null)
            return ret;
        if((ret = getHandlerNode(servletConf.getChild("exception-handler"), name)) != null)
            return ret;
        return getHandlerNode(requestConf.getChild("exception-handler"), name);
    }

    private Document getHandlerNode(Document node, String name) {
        return node != null ? Documents.findMatchesChild(node, "for", name) : null;
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
