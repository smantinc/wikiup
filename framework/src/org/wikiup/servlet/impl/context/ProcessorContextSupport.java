package org.wikiup.servlet.impl.context;

import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Wrapper;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.inf.ProcessorContext;

public class ProcessorContextSupport implements ProcessorContext, Wrapper<Object> {
    private Object instance;

    public ProcessorContextSupport(Object instance) {
        this.instance = instance;
    }

    public ProcessorContextSupport() {
        instance = this;
    }

    public Object get(String name) {
        return ContextUtil.getBeanProperty(instance, name);
    }

    public BeanContainer getModelContainer(String name, Getter<?> params) {
        return Interfaces.getModelContainer(ContextUtil.getBeanProperty(instance, name));
    }

    @Override
    public Object wrapped() {
        return instance;
    }
}