package org.wikiup.servlet.impl.mapping;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletConfigureMapping;

public class NamespaceServletMapping implements ServletConfigureMapping {
    private Document namespaces = Documents.create("namespace-mapping");

    public Boolean filter(String pattern) {
        return pattern.matches("[\\w\\.\\d-/]*/\\*");
    }

    public Document map(ServletProcessorContext context) {
        String[] paths = context.getRequestURI().split("/");
        Document doc = Util.getNamespaceNode(namespaces, paths);
        ServletMappingEntry node = doc != null ? (ServletMappingEntry) doc.getObject() : null;
        return node != null ? node.getDocument() : null;
    }

    public void appendEntry(String uriPattern, ServletMappingEntry node) {
        String[] paths = uriPattern.split("/");
        Document doc = Documents.touchDocument(namespaces, paths, paths.length - 1);
        doc.setObject(node);
    }
}
