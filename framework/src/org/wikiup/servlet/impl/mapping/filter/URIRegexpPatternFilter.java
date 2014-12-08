package org.wikiup.servlet.impl.mapping.filter;

import org.wikiup.core.inf.ext.LogicalFilter;
import org.wikiup.servlet.ServletProcessorContext;

import java.util.regex.Pattern;

public class URIRegexpPatternFilter implements LogicalFilter<ServletProcessorContext> {
    private Pattern pattern;

    public URIRegexpPatternFilter(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public Boolean translate(ServletProcessorContext context) {
        return pattern.matcher(context.getRequestURI()).matches();
    }
}
