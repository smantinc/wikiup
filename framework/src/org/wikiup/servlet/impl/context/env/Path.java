package org.wikiup.servlet.impl.context.env;

import org.wikiup.core.inf.Getter;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.beans.ServletContextContainer;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

@Deprecated
public class Path implements ServletProcessorContextAware, Getter<String> {
    private ServletProcessorContext context;

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return ServletContextContainer.getInstance().getRealPath(context.getRequestURI());
    }

    public String get(String name) {
        return ServletContextContainer.getInstance().getRealPath(context.getContextPath(name));
    }
}