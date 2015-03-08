package org.wikiup.servlet.impl.context;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupNamingDirectory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Translator;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ProcessorModelContainer;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class WikiupNamingDirectoryProcessorContext implements ProcessorContext, DocumentAware, ServletProcessorContextAware, BeanContainer {
    private ServletProcessorContext context;
    private Dictionary<?> directory;
    private String uri;

    public WikiupNamingDirectoryProcessorContext() {
    }

    public WikiupNamingDirectoryProcessorContext(String[] path) {
        directory = Wikiup.getInstance().get(Dictionary.class, path);
    }

    public BeanContainer getModelContainer(String name, Dictionary<?> params) {
        String[] path = StringUtil.splitNamespaces(name);
        if(path.length < 2)
            return Interfaces.getModelContainer(get(name));
        ProcessorModelContainer mc = Interfaces.cast(ProcessorModelContainer.class, Dictionaries.getProperty(getDirectory(), path, path.length - 1));
        if(mc != null)
            return mc.getModelContainer(path[path.length - 1], params);
        Object object = getContextAwaredObject(path);
        mc = Interfaces.cast(ProcessorModelContainer.class, object);
        return mc != null ? mc.getModelContainer(name, params) : Interfaces.getModelContainer(object);
    }

    public Object get(String name) {
        return getContextAwaredObject(StringUtil.splitNamespaces(name));
    }

    public void aware(Document desc) {
        uri = Documents.getDocumentValue(desc, "uri");
    }

    private Object getContextAwaredObject(String[] path) {
        if(context != null)
            return context.awaredBy(Dictionaries.getProperty(getDirectory(), path, path.length, 0, new LookupFilter()));
        return Dictionaries.getProperty(getDirectory(), path);
    }

    private Dictionary<?> getDirectory() {
        return Assert.notNull(directory != null ? directory : (directory = (Dictionary<?>) WikiupNamingDirectory.getInstance().get(uri)));
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    public <E> E query(Class<E> clazz) {
        BeanContainer mc = Interfaces.getModelContainer(getDirectory());
        return mc != null ? mc.query(clazz) : Interfaces.cast(clazz, this);
    }

    private class LookupFilter implements Translator<Dictionary<?>, Dictionary<?>> {
        public Dictionary<?> translate(Dictionary<?> object) {
            return context.awaredBy(object);
        }
    }
}
