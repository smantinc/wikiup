package org.wikiup.servlet.impl.context;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.I18nResourceManager;
import org.wikiup.core.impl.document.DocumentWrapper;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class I18nProcessorContext implements ProcessorContext, ServletProcessorContextAware, DocumentAware {
    private String locale;
    private ServletProcessorContext context;
    private I18nResourceManager manager;

    public Object get(String name) {
        Document doc = manager.getResource(locale, name);
        String value = ValueUtil.toString(doc);
        return value != null ? value : doc;
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    public void aware(Document desc) {
        I18nResourceManager i18n = Wikiup.getModel(I18nResourceManager.class);
        locale = context.getContextAttribute(desc, "locale", i18n.getAcceptLanguage(context.getServletRequest().getLocale()));
        manager = Wikiup.getModel(I18nResourceManager.class);
    }
    
    private static class I18nDocument extends DocumentWrapper {
        public I18nDocument(Document doc) {
            super(doc);
        }

        @Override
        public String toString() {
            return ValueUtil.toString(getDocument());
        }
    }
}