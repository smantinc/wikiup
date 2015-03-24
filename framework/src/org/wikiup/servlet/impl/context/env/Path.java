package org.wikiup.servlet.impl.context.env;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.beans.ServletContextContainer;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

@Deprecated
public class Path implements ServletProcessorContextAware, Dictionary<String> {
    private ServletProcessorContext context;

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return ServletContextContainer.getInstance().getRealPath(context.getContextPath(context.getRequestURI()));
    }

    public String get(String name) {
        return ServletContextContainer.getInstance().getRealPath(context.getContextPath(name));
    }
}