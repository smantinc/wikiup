package org.wikiup.core.bean;

import org.wikiup.core.impl.beancontainer.Singleton;

public class WikiupDynamicSingletons extends Singleton {
    static private WikiupDynamicSingletons instance = new WikiupDynamicSingletons();

    public WikiupDynamicSingletons() {
    }

    public static WikiupDynamicSingletons getInstance() {
        return instance;
    }
}
