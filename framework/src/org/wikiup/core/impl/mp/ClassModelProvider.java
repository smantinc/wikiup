package org.wikiup.core.impl.mp;

import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.util.Interfaces;

public class ClassModelProvider implements ModelProvider {
    public <E> E getModel(Class<E> clazz) {
        return Interfaces.newInstance(clazz);
    }
}
