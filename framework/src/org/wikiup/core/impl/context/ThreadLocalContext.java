package org.wikiup.core.impl.context;

import org.wikiup.core.inf.ext.Context;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadLocalContext implements Context<Object, Object>, Iterable<String> {
    private Map<String, ThreadLocal<Object>> threadLocals = new ConcurrentHashMap<String, ThreadLocal<Object>>();

    public Object get(String name) {
        ThreadLocal<Object> tl = threadLocals.get(name);
        return tl != null ? tl.get() : null;
    }

    public void set(String name, Object obj) {
        ThreadLocal<Object> tl = threadLocals.get(name);
        if(tl == null)
            threadLocals.put(name, (tl = new ThreadLocal<Object>()));
        tl.set(obj);
    }

    public Iterator<String> iterator() {
        return threadLocals.keySet().iterator();
    }

    public Map<String, ThreadLocal<Object>> getThreadLocals() {
        return threadLocals;
    }
}
