package org.wikiup.servlet.impl.mapping;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;

import java.util.Collection;
import java.util.Iterator;

class Util {
    static public Document getFilteredDocument(ServletProcessorContext context, Collection<ServletMappingEntry> set) {
        if(set != null) {
            Iterator<ServletMappingEntry> iterator = set.iterator();
            while(iterator.hasNext()) {
                ServletMappingEntry node = iterator.next();
                if(!node.isAbstract() && node.filter(context))
                    return node.getDocument();
            }
        }
        return null;
    }

    static public Document getNamespaceNode(Document namespaces, String[] paths) {
        int i;
        Document doc = namespaces;
        for(i = 0; i < paths.length; i++)
            if(!StringUtil.isEmpty(paths[i])) {
                Document child = doc.getChild(paths[i]);
                if(child == null)
                    return doc;
                doc = child;
            }
        return doc;
    }
}
