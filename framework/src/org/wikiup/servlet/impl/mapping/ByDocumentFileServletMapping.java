package org.wikiup.servlet.impl.mapping;

import org.wikiup.core.impl.resource.FileResource;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletConfigureMapping;

import java.io.File;

public class ByDocumentFileServletMapping implements ServletConfigureMapping {
    public Document map(ServletProcessorContext context) {
        File file = context.getAssociatedFile("xml");
        if(!file.exists())
            file = context.getAssociatedFile("yaml");
        return file.exists() ? Documents.loadFromResource(new FileResource(file)) : null;
    }

    public void appendEntry(String uriPattern, ServletMappingEntry node) {
    }

    public Boolean filter(String pattern) {
        return true;
    }
}
