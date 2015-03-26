package org.wikiup.extensions.wmdk.leprechaun;

import org.wikiup.Wikiup;
import org.wikiup.framework.bean.WikiupPluginManager;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class PluginSampleDisplayerProcessorContext implements ProcessorContext, ProcessorContext.ByParameters, ServletProcessorContextAware {
    private ServletProcessorContext context;

    @Override
    public Object get(String name) {
        return null;
    }

    @Override
    public Object get(String name, Dictionary<?> params) {
        String html;
        WikiupPluginManager.Plugin plugin = Wikiup.getModel(WikiupPluginManager.class).get(name);
        boolean isPlugin = ValueUtil.toBoolean(params.get("plugin"), false);
        if(plugin == null)
            html = StringUtil.evaluateEL("<li class=\"lp-plugin-disabled\"><a href=\"#\" onclick=\"pluginPromoteConfirm('" + name + "'); return false\" title=\"$tooltip\">$title</a></li>", params);
        else if(plugin.isDisabled())
            html = StringUtil.evaluateEL("<li class=\"lp-plugin-disabled\"><a href=\"#\" onclick=\"return false\" title=\"$tooltip\">$title</a></li>", params);
        else
            html = StringUtil.evaluateEL(isPlugin ? "<li class=\"lp-plugin-enabled\"><a href=\"" + context.getContextURI(ValueUtil.toString(plugin.get("index"))) + "\" target=\"_blank\">$title</a> </li>" : "<li class=\"lp-plugin-enabled\"><a href=\"#\" onclick=\"showExample(parentname + '/$category'); return false\">$title</a> </li>", params);
        return html;
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
