package org.wikiup.servlet.impl.processor;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DenyServletProcessor implements ServletProcessor, DocumentAware {
    private int reason;
    private String message;

    public void aware(Document cfg) {
        try {
            reason = HttpServletResponse.class.getField(
                    Documents.getAttributeValue(cfg, "reason")).getInt(null);
        } catch(Exception ex) {
            reason = HttpServletResponse.SC_FORBIDDEN;
        }
        message = Documents.getAttributeValue(cfg, "message", null);
    }

    public void process(ServletProcessorContext context) {
        try {
            context.getServletResponse().sendError(reason,
                    message != null ? message :
                            context.getRequestURI());
        } catch(IOException ex) {
            Assert.fail(ex);
        }
    }
}
