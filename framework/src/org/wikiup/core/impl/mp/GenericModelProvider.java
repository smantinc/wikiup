package org.wikiup.core.impl.mp;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ModelProvider;

public class GenericModelProvider implements ModelProvider {
    private ModelProvider modelProvider;

    public GenericModelProvider(Object obj) {
        if(obj instanceof ModelProvider)
            modelProvider = (ModelProvider) obj;
        else if(obj instanceof Iterable)
            modelProvider = new IterableModelProvider((Iterable<?>) obj);
        else if(obj instanceof Document)
            modelProvider = new DocumentModelProvider((Document) obj);
        else
            modelProvider = new InstanceModelProvider(obj);
    }

    public <E> E getModel(Class<E> clazz) {
        return modelProvider.getModel(clazz);
    }
}
