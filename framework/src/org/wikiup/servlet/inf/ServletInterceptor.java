package org.wikiup.servlet.inf;

import org.wikiup.servlet.ServletProcessorContext;

public interface ServletInterceptor {
    public boolean intercept(ServletProcessorContext context);
}
