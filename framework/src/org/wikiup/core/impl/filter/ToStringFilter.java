package org.wikiup.core.impl.filter;

import org.wikiup.core.inf.Translator;
import org.wikiup.core.util.ValueUtil;

public class ToStringFilter implements Translator<Object, String> {
    public String translate(Object object) {
        return ValueUtil.toString(object);
    }
}
