package org.wikiup.core.impl.mp;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.BeanContainer;

public class GenericModelProvider implements BeanContainer {
    private BeanContainer modelProvider;

    public GenericModelProvider(Object obj) {
        if(obj instanceof BeanContainer)
            modelProvider = (BeanContainer) obj;
        else if(obj instanceof Iterable)
            modelProvider = new IterableModelProvider((Iterable<?>) obj);
        else if(obj instanceof Document)
            modelProvider = new DocumentModelProvider((Document) obj);
        else
            modelProvider = new InstanceModelProvider(obj);
    }

    public <E> E query(Class<E> clazz) {
        return modelProvider.query(clazz);
    }
}
