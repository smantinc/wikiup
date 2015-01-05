package org.wikiup.core.impl.getter;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;

public class ModelContainerGetter implements Getter<Object> {
    private BeanFactory modelProvider;

    public ModelContainerGetter(BeanFactory modelProvider) {
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
