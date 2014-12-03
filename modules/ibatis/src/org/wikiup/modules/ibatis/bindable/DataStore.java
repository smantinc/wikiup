package org.wikiup.modules.ibatis.bindable;

import org.wikiup.core.impl.bindable.ByPropertyNames;
import org.wikiup.core.impl.context.MapContext;
import org.wikiup.core.impl.mp.ByTypeModelProvider;
import org.wikiup.core.inf.Bindable;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.exception.InsufficientPrimaryKeys;

import java.util.HashMap;
import java.util.Map;

public class DataStore implements ModelProvider, Bindable {
    private Map<String, Object> store = new HashMap<String, Object>();
    private Object binded = store;

    public void bind(Object object) {
        binded = object;
        if(object instanceof Map)
            store = (Map<String, Object>) object;
        else if(store != null) {
            new ByPropertyNames(new MapContext<Object>(store)).bind(binded);
            store = null;
        }
    }

    public <E> E getModel(Class<E> clazz) {
        E model = Interfaces.cast(clazz, binded);
        if(model == null)
            if(binded == store)
                bind(model = Interfaces.newInstance(clazz));
            else
                model = new ByTypeModelProvider(binded).getModel(clazz);
        Assert.notNull(model, InsufficientPrimaryKeys.class, binded, clazz.getName());
        return model;
    }

    public Map<String, Object> getStore() {
        return store;
    }
}