package org.wikiup.servlet.impl.processor;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.impl.document.InterceptableDocument;
import org.wikiup.servlet.inf.ServletProcessor;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.util.ArrayList;
import java.util.List;

public class ChainServletProcessor implements ServletProcessor, DocumentAware, ServletProcessorContextAware {
    private List<ServletProcessor> chain = new ArrayList<ServletProcessor>();
    private ServletProcessorContext context;

    public void append(ServletProcessor processor) {
        chain.add(processor);
    }

    public void process(ServletProcessorContext context) {
        for(ServletProcessor processor : chain)
            processor.process(context);
    }

    public void aware(Document cfg) {
        Document desc = new InterceptableDocument(context, cfg);
        for(Document doc : desc.getChildren())
            append(context.buildServletProcessor(doc));
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
