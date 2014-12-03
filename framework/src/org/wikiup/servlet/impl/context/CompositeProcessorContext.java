package org.wikiup.servlet.impl.context;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.util.LinkedList;

public class CompositeProcessorContext implements ProcessorContext, ServletProcessorContextAware, Getter<Object> {
    private LinkedList<ProcessorContext> contexts = new LinkedList<ProcessorContext>();

    public Object get(String name) {
        for(ProcessorContext ctx : contexts) {
            Object obj = ctx.get(name);
            if(obj != null)
                return obj;
        }
        return null;
    }

    public void append(ProcessorContext... args) {
        for(ProcessorContext arg : args)
            contexts.add(0, arg);
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        context.awaredBy(contexts.descendingIterator());
    }

    public ModelProvider getModelContainer(String name, Getter<?> params) {
        for(ProcessorContext ctx : contexts) {
            ModelProvider mc = ctx.getModelContainer(name, params);
            if(mc != null)
                return mc;
        }
        return null;
    }

    public LinkedList<ProcessorContext> getContexts() {
        return contexts;
    }

    public void setContexts(LinkedList<ProcessorContext> contexts) {
        this.contexts = contexts;
    }
}
