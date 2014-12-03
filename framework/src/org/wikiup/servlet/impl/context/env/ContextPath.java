package org.wikiup.servlet.impl.context.env;

import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class ContextPath implements ServletProcessorContextAware {
    protected ServletProcessorContext context;

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return context.getServletRequest().getContextPath();
    }
}
