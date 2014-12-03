package org.wikiup.servlet.impl.mapping;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bootstrap.inf.ExtendableDocumentResource;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletConfigureMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ServletMapping implements ServletConfigureMapping, DocumentAware, ExtendableDocumentResource<ServletMappingEntry> {
    private Collection<ServletConfigureMapping> mappers = new Vector<ServletConfigureMapping>();
    private String nodeName;

    public ServletMapping(String nodeName, Document doc) {
        this.nodeName = nodeName;
        aware(doc);
    }

    public Document map(ServletProcessorContext context) {
        for(ServletConfigureMapping mapper : mappers) {
            Document doc = mapper.map(context);
            if(doc != null)
                return doc;
        }
        return null;
    }

    public void aware(Document desc) {
        for(Document node : desc.getChildren("servlet-mapping"))
            mappers.add(Wikiup.build(ServletConfigureMapping.class, node, node));
    }

    public void appendEntry(String uriPattern, ServletMappingEntry node) {
        if(!node.isAbstract()) {
            Iterator<ServletConfigureMapping> iterator = mappers.iterator();
            while(iterator.hasNext()) {
                ServletConfigureMapping mapper = iterator.next();
                if(appendMatchedNode(uriPattern, node, mapper))
                    break;
            }
        }
    }

    private boolean appendMatchedNode(String uriPattern, ServletMappingEntry node, ServletConfigureMapping mapper) {
        boolean matched = mapper.filter(uriPattern);
        if(matched)
            mapper.appendEntry(uriPattern, node);
        else {
            String patterns[] = uriPattern.split("\\|");
            if(patterns.length > 1) {
                for(String pattern : patterns)
                    if(mapper.filter(pattern)) {
                        mapper.appendEntry(pattern, node);
                        matched = true;
                    }
            }
        }
        return matched;
    }

    public Boolean filter(String pattern) {
        return true;
    }

    public List<ServletMappingEntry> loadResources(Document doc) {
        List<ServletMappingEntry> list = new ArrayList<ServletMappingEntry>();
        for(Document node : doc.getChildren(nodeName))
            list.add(new ServletMappingEntry(node));
        return list;
    }

    public String getSuperResourceName(ServletMappingEntry entry) {
        return entry.getSuperName();
    }

    public String getResourceName(ServletMappingEntry doc) {
        return doc.getName();
    }

    public void extend(ServletMappingEntry thz, ServletMappingEntry sup) {
        thz.extend(sup);
    }

    public void finish(Map<String, ServletMappingEntry> hierary, List<ServletMappingEntry> resources) {
        for(ServletMappingEntry entry : resources)
            appendEntry(entry.getURIPattern(), entry);
    }
}
