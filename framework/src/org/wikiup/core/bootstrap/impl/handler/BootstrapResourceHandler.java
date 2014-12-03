package org.wikiup.core.bootstrap.impl.handler;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupPluginManager;
import org.wikiup.core.bootstrap.Bootstrap;
import org.wikiup.core.bootstrap.inf.ResourceHandler;
import org.wikiup.core.impl.resource.JarFileResource;
import org.wikiup.core.inf.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;

public class BootstrapResourceHandler implements ResourceHandler {
    public void handle(Resource resource) {
        try {
            Bootstrap.getInstance().handle(resource);
        } catch(Exception ex) {
            JarFileResource jarFile = Interfaces.cast(JarFileResource.class, resource);
            if(jarFile != null) {
                WikiupPluginManager.Plugin plugin = Wikiup.getModel(WikiupPluginManager.class).getPluginByJar(jarFile.getJar());
                if(plugin != null) {
                    plugin.setStatus(WikiupPluginManager.Plugin.Status.incompatible);
                    return;
                }
            }
            Assert.fail(ex);
        }
    }

    public void finish() {
        Bootstrap.getInstance().finish();
    }
}
