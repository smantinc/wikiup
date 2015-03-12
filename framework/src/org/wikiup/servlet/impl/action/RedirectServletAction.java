package org.wikiup.servlet.impl.action;

import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectServletAction implements ServletAction {
    public void doAction(ServletProcessorContext context, Document node) {
        HttpServletResponse response = context.getServletResponse();
        String url = context.getContextAttribute(node, "url", null);
        String uri = context.getContextAttribute(node, "uri", null);
        Assert.isTrue(url != null || uri != null, AttributeException.class, node, "(url, uri)");
        if(url != null) {
            try {
                response.sendRedirect(response.encodeRedirectURL(url.startsWith("/") ? context.getContextURI(url) : url));
            } catch(IOException ex) {
                Assert.fail(ex);
            }
        } else {
            context.setRequestURI(uri);
            context.doInit();
            context.doProcess();
            context.release();
        }
    }
}
