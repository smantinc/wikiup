package org.wikiup.servlet.impl.context.env;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.inf.Getter;

public class Configure implements Getter<String> {
    private WikiupConfigure inst = Wikiup.getModel(WikiupConfigure.class);

    public String get(String name) {
        String prop = inst.getProperty(name, null);
        return prop != null ? prop : inst.get(name);
    }
}
