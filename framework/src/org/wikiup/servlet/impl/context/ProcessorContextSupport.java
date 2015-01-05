package org.wikiup.servlet.impl.context;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.inf.Provider;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.inf.ProcessorContext;

public class ProcessorContextSupport implements ProcessorContext, Provider<Object> {
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

    public BeanFactory getModelContainer(String name, Getter<?> params) {
        return Interfaces.getModelContainer(ContextUtil.getBeanProperty(instance, name));
    }

    public Object get() {
        return instance;
    }
}