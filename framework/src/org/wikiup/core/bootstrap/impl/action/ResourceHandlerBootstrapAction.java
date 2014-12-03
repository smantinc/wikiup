package org.wikiup.core.bootstrap.impl.action;

import org.wikiup.core.bootstrap.Bootstrap;
import org.wikiup.core.bootstrap.BootstrapResource;
import org.wikiup.core.bootstrap.inf.ResourceHandler;
import org.wikiup.core.bootstrap.inf.ext.BootstrapAction;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.LogicalFilter;

public class ResourceHandlerBootstrapAction implements BootstrapAction {
    public void doAction(Bootstrap bootstrap, Document node) {
        ResourceHandler resourceHandler = bootstrap.build(ResourceHandler.class, node.getChild("handler"));
        BootstrapResource bootstrapResource = bootstrap.getBootstrapResource();
        for(Document f : node.getChildren("filter"))
            bootstrapResource.loadResources(resourceHandler, bootstrap.build(LogicalFilter.class, f));
    }
}