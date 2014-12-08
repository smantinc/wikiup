package org.wikiup.servlet.impl.mapping;

import org.wikiup.core.inf.Document;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.impl.mapping.filter.URIRegexpPatternFilter;
import org.wikiup.servlet.inf.ServletConfigureMapping;

import java.util.List;
import java.util.Vector;

public class URIRegexpPatternServletMapping implements ServletConfigureMapping {
    private List<ServletMappingEntry> nodes = new Vector<ServletMappingEntry>();

    public Document map(ServletProcessorContext context) {
        return Util.getFilteredDocument(context, nodes);
    }

    public void appendEntry(String uriPattern, ServletMappingEntry node) {
        node.addFilter(new URIRegexpPatternFilter(uriPattern));
        nodes.add(node);
    }

    public Boolean translate(String pattern) {
        return true;
    }

}
