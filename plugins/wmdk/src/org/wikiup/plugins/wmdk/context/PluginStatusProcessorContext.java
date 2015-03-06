package org.wikiup.plugins.wmdk.context;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupPluginManager;
import org.wikiup.core.impl.mp.InstanceModelProvider;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.servlet.inf.ProcessorContext;

public class PluginStatusProcessorContext implements ProcessorContext {
    public Object get(String name) {
        WikiupPluginManager pm = Wikiup.getModel(WikiupPluginManager.class);
        WikiupPluginManager.Plugin plugin = pm.get(name);
        return plugin != null && !plugin.isDisabled() ? "true" : "false";
    }

    public BeanContainer getModelContainer(String name, Dictionary<?> params) {
        WikiupPluginManager pm = Wikiup.getModel(WikiupPluginManager.class);
        WikiupPluginManager.Plugin plugin = pm.get(name);
        return plugin != null && !plugin.isDisabled() ? new InstanceModelProvider(plugin) : null;
    }
}
