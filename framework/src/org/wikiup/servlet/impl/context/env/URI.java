package org.wikiup.servlet.impl.context.env;

import org.wikiup.core.inf.Getter;

public class URI extends ContextPath implements Getter<String> {
    @Override
    public String toString() {
        return context.getServletRequest().getRequestURI();
    }

    public String get(String uri) {
        return context.getContextURI(uri);
    }
}
