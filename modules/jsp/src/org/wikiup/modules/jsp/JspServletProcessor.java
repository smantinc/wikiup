package org.wikiup.modules.jsp;

import org.wikiup.Wikiup;
import org.wikiup.core.util.Assert;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessor;

public class JspServletProcessor implements ServletProcessor {
    public void process(ServletProcessorContext context) {
        try {
            JspServletContainer container = Wikiup.getModel(JspServletContainer.class);
            JspHttpServletResponse response = new JspHttpServletResponse(context.getServletResponse(), context, container.getEl());
            JspServletContext.get().context = context;
            container.getJspServlet().service(context.getServletRequest(), response);
        } catch(Exception e) {
            Assert.fail(e);
        }
    }
}
