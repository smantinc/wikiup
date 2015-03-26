package org.wikiup.plugins.wmdk.context;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.impl.document.DocumentWithGetter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class UIExtensionProcessorContext implements ProcessorContext, ServletProcessorContextAware {
    private ServletProcessorContext context;

    @Override
    public Object get(String name) {
        Document configure = WikiupConfigure.getInstance().lookup(StringUtil.connect("wmdk/extension/ui", name, '/'));
        return new DocumentWithGetter(configure, context);
    }

    @Override
    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
