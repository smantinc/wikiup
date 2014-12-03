package org.wikiup.core.impl.mp;

import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.util.Interfaces;

public class InstanceModelProvider implements ModelProvider {
    private Object instance;

    public InstanceModelProvider() {
        instance = this;
    }

    public InstanceModelProvider(Object instance) {
        this.instance = instance;
    }

    public <E> E getModel(Class<E> clazz) {
        return Interfaces.cast(clazz, instance);
    }

    @Override
    public String toString() {
        return instance != null ? instance.toString() : super.toString();
    }
}