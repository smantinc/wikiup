package org.wikiup.core.impl.filter;

import org.wikiup.core.inf.Filter;
import org.wikiup.core.util.ValueUtil;

public class ToStringFilter implements Filter<Object, String> {
    public String filter(Object object) {
        return ValueUtil.toString(object);
    }
}
