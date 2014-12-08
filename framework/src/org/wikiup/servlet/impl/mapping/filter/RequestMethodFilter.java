package org.wikiup.servlet.impl.mapping.filter;

import org.wikiup.core.inf.ext.LogicalFilter;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;

import java.util.HashSet;
import java.util.Set;

public class RequestMethodFilter implements LogicalFilter<ServletProcessorContext> {
    private Set<String> methods = new HashSet<String>();

    public RequestMethodFilter(String method) {
        String m[] = method.split("[,\\|;]");
        int i;
        for(i = 0; i < m.length; i++)
            methods.add(StringUtil.trim(m[i]).toUpperCase());
    }

    public Boolean translate(ServletProcessorContext context) {
        return methods.contains(context.getServletRequest().getMethod());
    }
}
