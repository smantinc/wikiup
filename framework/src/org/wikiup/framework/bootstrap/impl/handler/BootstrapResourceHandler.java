package org.wikiup.framework.bootstrap.impl.handler;

import org.wikiup.Wikiup;
import org.wikiup.framework.bean.WikiupPluginManager;
import org.wikiup.framework.bootstrap.Bootstrap;
import org.wikiup.framework.bootstrap.inf.ResourceHandler;
import org.wikiup.core.impl.resource.JarFileResource;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.beans.ServletContextContainer;

public class BootstrapResourceHandler implements ResourceHandler {
    public void handle(Resource resource) {
        try {
            Bootstrap.getInstance().handle(resource);
        } catch(Throwable ex) {
            JarFileResource jarFile = Interfaces.cast(JarFileResource.class, resource);
            if(jarFile != null) {
                ServletContextContainer contextContainer = Wikiup.getModel(ServletContextContainer.class);
                contextContainer.log(BootstrapResourceHandler.class.getName(), ex);
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
