package org.wikiup.plugins.wmdk.context;

import org.wikiup.Wikiup;
import org.wikiup.framework.bean.WikiupPluginManager;
import org.wikiup.servlet.inf.ProcessorContext;

public class PluginStatusProcessorContext implements ProcessorContext {
    public Object get(String name) {
        WikiupPluginManager pm = Wikiup.getModel(WikiupPluginManager.class);
        WikiupPluginManager.Plugin plugin = pm.get(name);
        return plugin != null && !plugin.isDisabled() ? "true" : "false";
    }
}
