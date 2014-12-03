package org.wikiup.modules.jsp;

import org.wikiup.servlet.ServletProcessorContext;

public class JspServletContext {
    static private ThreadLocal<JspServletContext> threadlocal = new ThreadLocal<JspServletContext>();

    public ServletProcessorContext context;

    static public JspServletContext get() {
        JspServletContext ctx = threadlocal.get();
        if(ctx == null) {
            ctx = new JspServletContext();
            threadlocal.set(ctx);
        }
        return ctx;
    }
}
