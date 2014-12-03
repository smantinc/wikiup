package org.wikiup.servlet.impl.interceptor;

import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletInterceptor;

public class ParseQueryStringServletInterceptor implements ServletInterceptor {
    /**
     * It seems that request.getParameter won't work quite right when there are escaped unicode
     * characters in the query-string.
     * <p/>
     * So, parse it again.
     *
     * @param context ServletProcessorContext
     * @return boolean
     */
    public boolean intercept(ServletProcessorContext context) {
        context.parseQueryString(context.getServletRequest().getQueryString());
        return false;
    }
}
