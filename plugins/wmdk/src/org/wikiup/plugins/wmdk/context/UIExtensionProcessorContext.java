package org.wikiup.plugins.wmdk.context;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.impl.document.DocumentWithGetter;
import org.wikiup.core.impl.mp.DocumentModelProvider;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class UIExtensionProcessorContext implements ProcessorContext, ServletProcessorContextAware {
    private ServletProcessorContext context;

    public Object get(String name) {
        return null;
    }

    public BeanFactory getModelContainer(String name, Getter<?> params) {
        Document configure = WikiupConfigure.getInstance().lookup(StringUtil.connect("wmdk/extension/ui", name, '/'));
        return configure != null ? new DocumentModelProvider(new DocumentWithGetter(configure, context)) : null;
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
