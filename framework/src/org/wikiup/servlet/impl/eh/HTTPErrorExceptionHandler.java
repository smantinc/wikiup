package org.wikiup.servlet.impl.eh;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ExceptionHandler;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import javax.servlet.http.HttpServletResponse;

public class HTTPErrorExceptionHandler implements ExceptionHandler, DocumentAware, ServletProcessorContextAware {
    private int reason;
    private String message;
    private ServletProcessorContext context;

    public boolean handle(Exception exp) {
        try {
            context.getServletResponse().sendError(reason, message != null ? message : context.getRequestURI());
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        return true;
    }

    public void aware(Document desc) {
        try {
            String re = Documents.getAttributeValue(desc, "reason", "500");
            reason = ValueUtil.toInteger(re, -1);
            reason = reason == -1 ? HttpServletResponse.class.getField(re).getInt(null) : reason;
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        message = Documents.getAttributeValue(desc, "message", null);
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
