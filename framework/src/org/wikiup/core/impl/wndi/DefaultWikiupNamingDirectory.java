package org.wikiup.core.impl.wndi;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultWikiupNamingDirectory implements Context<Object, Object>, DocumentAware, Releasable, Iterable<String> {
    private Map<String, Object> map = new HashMap<String, Object>();

    public void aware(Document desc) {
        Wikiup.getInstance().loadBeans(Object.class, this, desc);
    }

    public void release() {
        for(Object obj : map.values())
            Interfaces.release(Interfaces.get(obj));
        map.clear();
    }

    public Object get(String name) {
        return Interfaces.get(map.get(name));
    }

    public void set(String name, Object object) {
        if(object instanceof WikiupDynamicSingleton)
            object = ((WikiupDynamicSingleton) object).getInstanceContainer();
        map.put(name, object);
    }

    public Iterator<String> iterator() {
        return map.keySet().iterator();
    }
}
