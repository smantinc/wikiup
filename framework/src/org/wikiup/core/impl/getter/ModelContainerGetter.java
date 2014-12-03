package org.wikiup.core.impl.getter;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.ModelProvider;

public class ModelContainerGetter implements Getter<Object> {
    private ModelProvider modelProvider;

    public ModelContainerGetter(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    public Object get(String name) {
        try {
            return modelProvider.getModel(Class.forName(name));
        } catch(ClassNotFoundException e) {
        }
        return null;
    }
}
