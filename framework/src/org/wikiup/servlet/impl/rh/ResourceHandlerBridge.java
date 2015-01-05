package org.wikiup.servlet.impl.rh;

import org.wikiup.core.bootstrap.inf.ResourceHandler;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessor;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class ResourceHandlerBridge implements org.wikiup.servlet.inf.ext.ResourceHandler, ServletProcessorContextAware {
    private ResourceHandler resourceHandler;
    private ServletProcessor processor;

    public ResourceHandlerBridge() {
    }

    public ResourceHandlerBridge(Object obj) {
        resourceHandler = Interfaces.cast(ResourceHandler.class, obj);
        processor = Interfaces.cast(ServletProcessor.class, obj);
    }

    public void handle(Resource resource) {
        if(resourceHandler != null)
            resourceHandler.handle(resource);
    }

    public void finish() {
        if(resourceHandler != null)
            resourceHandler.finish();
    }

    public void process(ServletProcessorContext context) {
        if(processor != null)
            processor.process(context);
    }

    public void setResourceHandler(ResourceHandler resourceHandler) {
        this.resourceHandler = resourceHandler;
    }

    public void setProcessor(ServletProcessor processor) {
        this.processor = processor;
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        context.awaredBy(resourceHandler);
        context.awaredBy(processor);
    }
}
