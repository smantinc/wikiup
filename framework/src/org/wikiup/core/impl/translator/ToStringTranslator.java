package org.wikiup.core.impl.translator;

import org.wikiup.core.inf.Translator;
import org.wikiup.core.util.ValueUtil;

public class ToStringTranslator implements Translator<Object, String> {
    public String translate(Object object) {
        return ValueUtil.toString(object);
    }
}
