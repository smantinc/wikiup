package org.wikiup.servlet.inf;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;

public interface ProcessorModelContainer {
    public BeanContainer getModelContainer(String name, Dictionary<?> params);
}
