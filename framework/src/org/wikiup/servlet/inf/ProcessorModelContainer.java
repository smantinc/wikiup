package org.wikiup.servlet.inf;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.ModelProvider;

public interface ProcessorModelContainer {
    public ModelProvider getModelContainer(String name, Getter<?> params);
}
