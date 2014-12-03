package org.wikiup.servlet.impl.eh;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ExceptionHandler;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectExceptionHandler implements ExceptionHandler, DocumentAware, ServletProcessorContextAware {
    private String url;
    private ServletProcessorContext context;

    public boolean handle(Exception exp) {
        HttpServletResponse response = context.getServletResponse();
        try {
            response.sendRedirect(response.encodeRedirectURL(context.getContextURI(StringUtil.evaluateEL(url, context))));
        } catch(IOException ex) {
            Assert.fail(ex);
        }
        return true;
    }

    public void aware(Document desc) {
        url = Documents.getAttributeValue(desc, "url");
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
