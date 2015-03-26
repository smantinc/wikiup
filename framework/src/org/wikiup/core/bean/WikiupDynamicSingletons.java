package org.wikiup.core.bean;

import org.wikiup.core.impl.beancontainer.Singleton;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.Wirable;

public class WikiupDynamicSingletons extends Singleton {
    static private WikiupDynamicSingletons instance = new WikiupDynamicSingletons();

    public WikiupDynamicSingletons() {
    }

    public static WikiupDynamicSingletons getInstance() {
        return instance;
    }
    
    public static final class WIRABLE implements Wirable.ByDocument<WikiupDynamicSingletons> {
        @Override
        public WikiupDynamicSingletons wire(Document param) {
            return instance;
        }
    }
}
