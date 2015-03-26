package org.wikiup.servlet.impl.context.env;

import org.wikiup.Wikiup;
import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.inf.Dictionary;

public class Configure implements Dictionary<String> {
    private WikiupConfigure inst = Wikiup.getModel(WikiupConfigure.class);

    public String get(String name) {
        String prop = inst.getProperty(name, null);
        return prop != null ? prop : inst.get(name);
    }
}
