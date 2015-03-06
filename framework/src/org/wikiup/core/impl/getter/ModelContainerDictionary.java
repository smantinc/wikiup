package org.wikiup.core.impl.getter;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;

public class ModelContainerDictionary implements Dictionary<Object> {
    private BeanContainer modelProvider;

    public ModelContainerDictionary(BeanContainer modelProvider) {
        this.modelProvider = modelProvider;
    }

    public Object get(String name) {
        try {
            return modelProvider.query(Class.forName(name));
        } catch(ClassNotFoundException e) {
        }
        return null;
    }
}
