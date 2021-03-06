package org.wikiup.modules.worms;

import org.wikiup.framework.bootstrap.inf.ResourceHandler;
import org.wikiup.core.inf.ext.Resource;

import java.util.HashSet;
import java.util.Set;

public class WormsEntityResourceHandler implements ResourceHandler {
    private Set<Resource> resources = new HashSet<Resource>();

    public void finish() {
        WormsEntityManager.getInstance().initialize(resources);
    }

    public void handle(Resource resource) {
        resources.add(resource);
    }
}
