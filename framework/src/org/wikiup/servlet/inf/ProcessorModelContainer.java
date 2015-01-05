package org.wikiup.servlet.inf;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;

public interface ProcessorModelContainer {
    public BeanFactory getModelContainer(String name, Getter<?> params);
}
