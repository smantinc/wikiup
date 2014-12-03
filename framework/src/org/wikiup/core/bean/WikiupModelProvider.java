package org.wikiup.core.bean;

import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WikiupModelProvider extends WikiupDynamicSingleton<WikiupModelProvider> implements ModelProvider, Context<ModelProvider, ModelProvider>, Iterable<String>, Releasable {
    private Map<String, ModelProvider> modelContainers;
    private WikiupDynamicSingletons singletons;

    public void firstBuilt() {
        modelContainers = new HashMap<String, ModelProvider>();
        singletons = new WikiupDynamicSingletons();
    }

    public <E> E getModel(Class<E> clazz) {
        E e;
        for(ModelProvider mc : modelContainers.values())
            if((e = mc.getModel(clazz)) != null)
                return e;
        return singletons != null ? singletons.getModel(clazz) : null;
    }

    public ModelProvider get(String name) {
        return modelContainers.get(name);
    }

    public void set(String name, ModelProvider obj) {
        modelContainers.put(name, obj);
        if(singletons != null && singletons.getClass().equals(obj.getClass()))
            singletons = null;
    }

    public Iterator<String> iterator() {
        return modelContainers.keySet().iterator();
    }

    public void release() {
        Interfaces.release(singletons);
        Interfaces.releaseAll(modelContainers.values());
    }
}
