package org.wikiup.servlet.impl.mapping;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletConfigureMapping;

public class NamespaceAndSuffixServletMapping implements ServletConfigureMapping {
    private Document namespaces = Documents.create("namespace-suffix-mapping");

    public Boolean translate(String pattern) {
        return pattern.matches("[\\w\\.\\d-/]+/\\*\\.[\\w\\d]+");
    }

    public Document map(ServletProcessorContext context) {
        String[] paths = context.getRequestURI().split("/");
        Document doc = Util.getNamespaceNode(namespaces, paths);
        String suffix = FileUtil.getFileExt(context.getRequestURI());
        ServletMappingEntry node = doc != null ? getMappingEntry(doc, suffix) : null;
        return node != null ? node.getDocument() : null;
    }

    public void appendEntry(String uriPattern, ServletMappingEntry node) {
        String[] paths = uriPattern.split("/");
        Document doc = Documents.touchDocument(namespaces, paths, paths.length - 1);
        Documents.setAttributeValue(doc, FileUtil.getFileExt(uriPattern), node);
    }

    private ServletMappingEntry getMappingEntry(Document doc, String suffix) {
        if(doc != null) {
            Attribute attr = doc.getAttribute(suffix);
            return attr != null ? (ServletMappingEntry) attr.getObject() : null;
        }
        return null;
    }
}
