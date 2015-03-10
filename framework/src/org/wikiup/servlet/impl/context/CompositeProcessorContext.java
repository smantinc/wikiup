package org.wikiup.servlet.impl.context;

import java.util.LinkedList;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class CompositeProcessorContext implements ProcessorContext, ServletProcessorContextAware {
    private LinkedList<Node> contexts = new LinkedList<Node>();

    public Object get(String name) {
        for(Node ctx : contexts) {
            Object obj = ctx.get(name);
            if(obj != null)
                return obj;
        }
        return null;
    }

    public void append(ProcessorContext... args) {
        for(ProcessorContext arg : args)
            contexts.add(0, new Node(arg));
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        context.awaredBy(contexts.descendingIterator());
    }

    public BeanContainer getModelContainer(String name, Dictionary<?> params) {
        for(ProcessorContext ctx : contexts) {
            BeanContainer mc = ctx.getModelContainer(name, params);
            if(mc != null)
                return mc;
        }
        return null;
    }

    public LinkedList<Node> getContexts() {
        return contexts;
    }

    private static class Node extends WrapperImpl<ProcessorContext> implements ProcessorContext, ProcessorContext.ByParameters {
        private ProcessorContext.ByParameters contextByParameters;
        
        public Node(ProcessorContext context) {
            super(context);
            this.contextByParameters = Interfaces.unwrap(ProcessorContext.ByParameters.class, context);
        }
        
        @Override
        public Object get(String name, Dictionary<?> params) {
            return contextByParameters != null ? contextByParameters.get(name, params) : null;
        }

        @Override
        public Object get(String name) {
            return wrapped.get(name);
        }

        @Override
        public BeanContainer getModelContainer(String name, Dictionary<?> params) {
            return wrapped.getModelContainer(name, params);
        }
    }
}
