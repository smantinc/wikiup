package org.wikiup.servlet.impl.mapping.filter;

import org.wikiup.core.inf.ext.LogicalTranslator;
import org.wikiup.servlet.ServletProcessorContext;

import java.util.regex.Pattern;

public class URIRegexpPatternTranslator implements LogicalTranslator<ServletProcessorContext> {
    private Pattern pattern;

    public URIRegexpPatternTranslator(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public Boolean translate(ServletProcessorContext context) {
        return pattern.matcher(context.getRequestURI()).matches();
    }
}
