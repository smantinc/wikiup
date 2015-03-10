package org.wikiup.servlet.impl.context;

import java.util.HashMap;
import java.util.Map;

import org.wikiup.core.Constants;
import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;
import org.wikiup.servlet.util.ProcessorContexts;

public class NamespaceProcessorContext implements ProcessorContext, ProcessorContext.ByParameters, ServletProcessorContextAware, Context<Object, Object>, DocumentAware {
    private Map<String, ProcessorContext> contexts = new HashMap<String, ProcessorContext>();
    private ServletProcessorContext context;

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
        context.awaredBy(contexts.values().iterator());
    }

    public void append(String name, ProcessorContext context) {
        contexts.put(name, context);
    }

    @Override
    public Object get(String name, Dictionary<?> params) {
        String path[] = StringUtil.splitNamespaces(name);
        ProcessorContext ctx = getNamespace(path[0]);
        return ctx != null ? (path.length > 1 ? ProcessorContexts.get(ctx, name.substring(path[0].length() + 1), params) : ctx) : null;
    }

    @Override
    public Object get(String name) {
        String path[] = StringUtil.splitNamespaces(name);
        ProcessorContext ctx = getNamespace(path[0]);
        return ctx != null ? Dictionaries.getProperty(ctx, path, path.length, 1) : ctx;
    }

    @Override
    public void set(String name, Object value) {
        String path[] = StringUtil.splitNamespaces(name);
        ProcessorContext ctx = getNamespace(path[0]);
        if(ctx != null)
            Interfaces.set(ctx, name.substring(path[0].length() + 1), value);
    }

    public ProcessorContext getNamespace(String name) {
        return contexts.get(name);
    }

    public void aware(Document desc) {
        for(Document node : desc.getChildren()) {
            String name = Documents.ensureAttributeValue(node, Constants.Attributes.NAME);
            ProcessorContext obj;
            BeanContainer mc = null;
            if(context != null) {
                mc = context.buildProcessorContextModelContainer(node);
                obj = mc.query(ProcessorContext.class);
                Assert.notNull(obj, IllegalArgumentException.class, name);
            } else
                obj = Wikiup.getInstance().getBean(ProcessorContext.class, node);
            append(name, obj);
            if(context != null)
                context.awaredBy(obj);
            if(mc != null)
                Interfaces.initialize(mc, node);
            else
                Interfaces.initialize(obj, node);
        }
    }
}
