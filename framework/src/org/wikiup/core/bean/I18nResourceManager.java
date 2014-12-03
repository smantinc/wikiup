package org.wikiup.core.bean;

import org.wikiup.core.impl.document.DocumentImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;

import java.util.Locale;

public class I18nResourceManager extends WikiupDynamicSingleton<I18nResourceManager> implements Getter<Object> {
    private static final String DEFAULT_ACCEPT_LANGUAGE = "en-us";

    private Document resource;

    public static I18nResourceManager getInstance() {
        return getInstance(I18nResourceManager.class);
    }

    public void firstBuilt() {
        resource = Documents.create("i18n");
    }

    public Document getResource(String locate) {
        return resource.getChild(locate);
    }

    public Document getResource(String locate, String path) {
        String paths[] = StringUtil.splitNamespaces(path);
        Document loc = resource.getChild(resource.getChild(locate) != null ? locate : DEFAULT_ACCEPT_LANGUAGE);
        Assert.notNull(loc);
        return Documents.getDocumentByPath(loc, paths, paths.length);
    }

    public Getter<String> getLanguageBundle(Locale locale) {
        Document resource = getResource(getAcceptLanguage(locale));
        return new LanguageBundle(resource);
    }

    public String getAcceptLanguage(Locale locale) {
        String acceptLanguage = locale != null ? locale.toString().toLowerCase().replace('_', '-') : DEFAULT_ACCEPT_LANGUAGE;
        return getAcceptLanguage(acceptLanguage);
    }

    public String getAcceptLanguage(String acceptLanguage) {
        if(acceptLanguage != null) {
            if(resource.getChild(acceptLanguage) != null)
                return acceptLanguage;
            if(resource.getChild(acceptLanguage.toLowerCase()) != null)
                return acceptLanguage.toLowerCase();
        }
        return DEFAULT_ACCEPT_LANGUAGE;
    }

    public Object get(String name) {
        return Documents.getDocumentValueByXPath(resource, name);
    }

    public String get(String locate, String path) {
        Document doc = getResource(locate, path);
        return ValueUtil.toString(doc);
    }

    public void mergeConfigure(String path[], Document doc) {
        DocumentImpl node = (DocumentImpl) Documents.touchDocument(resource, path, path.length);
        for(Document child : doc.getChildren())
            node.attachChild(child);
    }

    private static class LanguageBundle implements Getter<String> {
        private Document resource;

        public LanguageBundle(Document resource) {
            this.resource = resource;
        }

        public String get(String name) {
            String paths[] = StringUtil.splitNamespaces(name);
            return ValueUtil.toString(Documents.getDocumentByPath(resource, paths, paths.length));
        }
    }
}