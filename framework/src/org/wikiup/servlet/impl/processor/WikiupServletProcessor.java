package org.wikiup.servlet.impl.processor;

import org.wikiup.core.inf.Resource;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessor;
import org.wikiup.servlet.util.ServletUtil;

public class WikiupServletProcessor implements ServletProcessor {
    public void process(ServletProcessorContext context) {
        Resource resource = context.getResponseBuffer().getResource();
        String ext = FileUtil.getFileExt(resource.getURI());
        ServletProcessor processor = Interfaces.cast(ServletProcessor.class, ServletUtil.getServletProcessorByExtension().get(ext));
        if(processor != null)
            processor.process(context);
    }
}
