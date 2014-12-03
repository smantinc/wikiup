package org.wikiup.servlet.impl.context;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupNamingDirectory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Filter;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ProcessorModelContainer;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class WikiupNamingDirectoryProcessorContext implements ProcessorContext, DocumentAware, ServletProcessorContextAware, ModelProvider {
    private ServletProcessorContext context;
    private Getter<?> directory;
    private String uri;

    public WikiupNamingDirectoryProcessorContext() {
    }

    public WikiupNamingDirectoryProcessorContext(String[] path) {
        directory = Wikiup.getInstance().get(Getter.class, path);
    }

    public ModelProvider getModelContainer(String name, Getter<?> params) {
        String[] path = StringUtil.splitNamespaces(name);
        if(path.length < 2)
            return Interfaces.getModelContainer(get(name));
        ProcessorModelContainer mc = Interfaces.cast(ProcessorModelContainer.class, ContextUtil.getProperty(getDirectory(), path, path.length - 1));
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
            return context.awaredBy(ContextUtil.getProperty(getDirectory(), path, path.length, 0, new LookupFilter()));
        return ContextUtil.getProperty(getDirectory(), path);
    }

    private Getter<?> getDirectory() {
        return Assert.notNull(directory != null ? directory : (directory = (Getter<?>) WikiupNamingDirectory.getInstance().get(uri)));
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    public <E> E getModel(Class<E> clazz) {
        ModelProvider mc = Interfaces.getModelContainer(getDirectory());
        return mc != null ? mc.getModel(clazz) : Interfaces.cast(clazz, this);
    }

    private class LookupFilter implements Filter<Getter<?>, Getter<?>> {
        public Getter<?> filter(Getter<?> object) {
            return context.awaredBy(object);
        }
    }
}
