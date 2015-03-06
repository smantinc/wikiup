package org.wikiup.servlet.impl;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.servlet.inf.ProcessorContext;

public class NullProcessorContext implements ProcessorContext {
    private static NullProcessorContext instance = null;

    public static NullProcessorContext getInstance() {
        return instance == null ? (instance = new NullProcessorContext()) : instance;
    }

    public BeanContainer getModelContainer(String name, Dictionary<?> params) {
        return null;
    }

    public Object get(String name) {
        return null;
    }

}
