package org.wikiup.servlet.inf;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.LogicalFilter;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.impl.mapping.ServletMappingEntry;

public interface ServletConfigureMapping extends LogicalFilter<String> {
    public Document map(ServletProcessorContext context);

    public void appendEntry(String uriPattern, ServletMappingEntry node);
}
