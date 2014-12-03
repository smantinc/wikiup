package org.wikiup.core.bean;

import org.wikiup.core.impl.mp.SingletonModelProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WikiupDynamicSingletons extends SingletonModelProvider {
    static private Map<Class<?>, Object> byClasses = new HashMap<Class<?>, Object>();
    static private Set<Object> singletons = new HashSet<Object>();
    static private WikiupDynamicSingletons instance = null;

    public WikiupDynamicSingletons() {
        setByClasses(byClasses);
        setSingletons(singletons);
    }

    public static WikiupDynamicSingletons getInstance() {
        return instance != null ? instance : (instance = new WikiupDynamicSingletons());
    }
}
