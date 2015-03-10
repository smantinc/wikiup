package org.wikiup.servlet.impl.context;

import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Translator;
import org.wikiup.core.inf.Wrapper;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Dictionaries;
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
        return Dictionaries.getBeanProperty(instance, name);
    }

    @Override
    public Object wrapped() {
        return instance;
    }
    
    public static final class TRANSLATOR implements Translator<Object, Object> {
        @Override
        public Object translate(Object obj) {
            ProcessorContext processorContext = Interfaces.cast(ProcessorContext.class, obj);
            return processorContext != null ? processorContext : obj instanceof Wirable ? obj : new ProcessorContextSupport(obj);
        }
    }
}