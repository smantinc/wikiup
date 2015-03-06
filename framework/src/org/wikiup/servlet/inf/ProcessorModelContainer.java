package org.wikiup.servlet.inf;

import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;

@Deprecated
public interface ProcessorModelContainer {
    public BeanContainer getModelContainer(String name, Dictionary<?> params);
}
