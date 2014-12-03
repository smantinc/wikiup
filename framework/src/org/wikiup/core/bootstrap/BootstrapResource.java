package org.wikiup.core.bootstrap;

import org.wikiup.core.bootstrap.inf.ResourceHandler;
import org.wikiup.core.impl.iterable.IterableCollection;
import org.wikiup.core.impl.resource.OverridedResource;
import org.wikiup.core.inf.Resource;
import org.wikiup.core.inf.ext.LogicalFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BootstrapResource implements Iterable<Resource> {
    private Map<String, Set<Resource>> resources = new HashMap<String, Set<Resource>>();
    private Set<String> uris;

    public Iterator<Resource> iterator() {
        IterableCollection<Resource> iterable = new IterableCollection<Resource>();
        for(Set<Resource> list : resources.values())
            iterable.append(list);
        return iterable.iterator();
    }

    public void doPackage() {
        uris = new HashSet<String>(resources.keySet());
    }

    public void append(Resource resource, boolean override) {
        String uri = resource.getURI();
        Set<Resource> set = resources.get(uri);
        if(set == null)
            resources.put(uri, set = new HashSet<Resource>());
        else if(uris != null && override && uris.contains(uri)) {
            resource = new OverridedResource(resource, set);
            resources.put(uri, set = new HashSet<Resource>());
            uris.remove(uri);
        }
        set.add(resource);
    }

    public void loadResources(ResourceHandler resourceHandler, LogicalFilter<String> filter) {
        try {
            Iterator<Resource> iterator = iterator();
            while(iterator.hasNext()) {
                Resource resource = iterator.next();
                if(filter == null || filter.filter(resource.getURI())) {
                    resourceHandler.handle(resource);
                    iterator.remove();
                }
            }
        } finally {
            resourceHandler.finish();
        }
    }
}