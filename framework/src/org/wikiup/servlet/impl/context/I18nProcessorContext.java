package org.wikiup.servlet.impl.context;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.I18nResourceManager;
import org.wikiup.core.impl.mp.InstanceModelProvider;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class I18nProcessorContext implements ProcessorContext, ServletProcessorContextAware, DocumentAware {
    private String locale;
    private ServletProcessorContext context;

    public ModelProvider getModelContainer(String name, Getter<?> params) {
        return new InstanceModelProvider(new I18nProcessorContextGetter(name));
    }

    public Object get(String name) {
        return I18nResourceManager.getInstance().get(locale, name);
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    public void aware(Document desc) {
        I18nResourceManager i18n = Wikiup.getModel(I18nResourceManager.class);
        locale = context.getContextAttribute(desc, "locale", i18n.getAcceptLanguage(context.getServletRequest().getLocale()));
    }

    private class I18nProcessorContextGetter implements Getter<String> {
        private String root;

        public I18nProcessorContextGetter(String root) {
            this.root = root;
        }

        public String get(String name) {
            return I18nResourceManager.getInstance().get(locale, StringUtil.connect(root, name, '.'));
        }
    }
}