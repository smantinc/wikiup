package org.wikiup.framework.bootstrap.impl.action;

import org.wikiup.framework.bootstrap.Bootstrap;
import org.wikiup.framework.bootstrap.BootstrapResource;
import org.wikiup.framework.bootstrap.inf.ResourceHandler;
import org.wikiup.framework.bootstrap.inf.ext.BootstrapAction;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.LogicalTranslator;

public class ResourceHandlerBootstrapAction implements BootstrapAction {
    public void doAction(Bootstrap bootstrap, Document node) {
        ResourceHandler resourceHandler = bootstrap.build(ResourceHandler.class, node.getChild("handler"));
        BootstrapResource bootstrapResource = bootstrap.getBootstrapResource();
        for(Document f : node.getChildren("filter"))
            bootstrapResource.loadResources(resourceHandler, bootstrap.build(LogicalTranslator.class, f));
    }
}