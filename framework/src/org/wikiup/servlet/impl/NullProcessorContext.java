package org.wikiup.servlet.impl;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.servlet.inf.ProcessorContext;

public class NullProcessorContext implements ProcessorContext {
    private static NullProcessorContext instance = null;

    public static NullProcessorContext getInstance() {
        return instance == null ? (instance = new NullProcessorContext()) : instance;
    }

    public BeanFactory getModelContainer(String name, Getter<?> params) {
        return null;
    }

    public Object get(String name) {
        return null;
    }

}
