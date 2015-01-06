package org.wikiup.servlet.inf;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanContainer;

public interface ProcessorModelContainer {
    public BeanContainer getModelContainer(String name, Getter<?> params);
}
