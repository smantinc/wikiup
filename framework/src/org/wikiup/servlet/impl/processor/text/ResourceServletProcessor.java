package org.wikiup.servlet.impl.processor.text;

import javax.servlet.http.HttpServletResponse;

import org.wikiup.core.impl.iterable.ArrayItems;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.exception.WikiupServletException;
import org.wikiup.servlet.impl.rh.ResourceHandlerBridge;
import org.wikiup.servlet.inf.ServletProcessor;
import org.wikiup.servlet.inf.ext.ResourceHandler;
import org.wikiup.servlet.util.ServletUtil;

public class ResourceServletProcessor implements ServletProcessor, DocumentAware {
    private Document configure;

    public void aware(Document desc) {
        configure = desc;
    }

    public void process(ServletProcessorContext context) {
        String ext = '.' + FileUtil.getFileExt(context.getRequestURI());
        String name = Documents.getDocumentValue(configure, "src", null);
        String suffix = Documents.getDocumentValue(configure, "suffix", null);
        Dictionary<?> processors = ServletUtil.getServletProcessorByExtension();
        if(name == null) {
            String[] exts = suffix != null ? suffix.split("[,\\|]") : null;
            doProcess(context, processors, context.getRequestPath(true), ext, exts);
        } else
            doProcess(context, processors, name);
    }

    private void doProcess(ServletProcessorContext context, Dictionary<?> processors, String pathName, String ext, String[] suffixs) {
        Resource resource;
        String name = StringUtil.connect(pathName, ext, '.');
        if(!(resource = context.getResource(name)).exists()) {
            Iterable<String> iterable = suffixs != null ? new ArrayItems<String>(suffixs) : Interfaces.<String>foreach(processors);
            for(String suffix : iterable) {
                name = StringUtil.connect(pathName, suffix, '.');
                if((resource = context.getResource(name)) != null)
                    break;
            }
        }
        boolean exists = resource != null && resource.exists();
        Assert.isTrue(exists, WikiupServletException.class, HttpServletResponse.SC_NOT_FOUND, "URL '{0}{1}' not found", pathName, ext);
        doProcess(context, processors, resource);
    }

    private void doProcess(ServletProcessorContext context, Dictionary<?> processors, String uri) {
        if(uri != null)
            doProcess(context, processors, context.getResource(uri));
    }

    private void doProcess(ServletProcessorContext context, Dictionary<?> processors, Resource resource) {
        String ext = FileUtil.getFileExt(resource.getURI());
        String resourceHandler = Documents.getDocumentValue(configure, "resource-handler", null);
        ResourceHandler handler = new ResourceHandlerBridge(processors.get(resourceHandler != null ? resourceHandler : ext));
        context.awaredBy(handler);
        handler.handle(resource);
        handler.finish();
        context.getResponseBuffer().setResource(resource);
    }
}