package org.wikiup.servlet.impl.context.sys;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.ValueUtil;

public class Now implements Dictionary<Long> {
    @Override
    public String toString() {
        return String.valueOf(System.currentTimeMillis());
    }

    public Long get(String name) {
        return System.currentTimeMillis() + ValueUtil.getTimeMillis(name);
    }
}
