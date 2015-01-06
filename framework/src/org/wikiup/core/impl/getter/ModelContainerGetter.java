package org.wikiup.core.impl.getter;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanContainer;

public class ModelContainerGetter implements Getter<Object> {
    private BeanContainer modelProvider;

    public ModelContainerGetter(BeanContainer modelProvider) {
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
