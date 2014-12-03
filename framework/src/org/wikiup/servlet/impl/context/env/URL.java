package org.wikiup.servlet.impl.context.env;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.StringUtil;

public class URL extends ContextPath implements Getter<String> {
    @Override
    public String toString() {
        return context.getRequestURL();
    }

    public String get(String name) {
        return StringUtil.connect(context.getRequestRoot(), context.getContextURI(name), '/');
    }
}
