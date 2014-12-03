package org.wikiup.servlet.impl.mapping;

import org.wikiup.core.inf.Document;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletConfigureMapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractServletMapping implements ServletConfigureMapping {
    private Map<String, Set<ServletMappingEntry>> map = new HashMap<String, Set<ServletMappingEntry>>();

    public void appendEntry(String uriPattern, ServletMappingEntry node) {
        String key = getMappingKey(uriPattern);
        Set<ServletMappingEntry> set = map.get(key);
        if(set == null)
            map.put(key, (set = new HashSet<ServletMappingEntry>()));
        set.add(node);
    }

    public Document map(ServletProcessorContext context) {
        return Util.getFilteredDocument(context, map.get(getMappingKey(context.getRequestURI())));
    }

    abstract protected String getMappingKey(String uriPattern);
}
