package org.wikiup.framework.bean;

import org.wikiup.Wikiup;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.framework.bootstrap.Bootstrap;
import org.wikiup.core.impl.context.MapContext;
import org.wikiup.core.impl.wndi.DefaultWikiupNamingDirectory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;

import java.util.Iterator;

public class WikiupNamingDirectory extends WikiupDynamicSingleton<WikiupNamingDirectory> implements Context<Object, Object>, Iterable<String> {
    private MapContext<Object> entries;

    static public WikiupNamingDirectory getInstance() {
        return getInstance(WikiupNamingDirectory.class);
    }

    public Object get(String name) {
        return name != null ? get(StringUtil.splitNamespaces(name)) : null;
    }

    public Object get(String[] path) {
        return Interfaces.get(Dictionaries.getProperty(entries, path, path.length));
    }

    public Object get(String[] path, int depth) {
        return Interfaces.get(Dictionaries.getProperty(entries, path, depth));
    }

    public void set(String name, Object obj) {
        set(StringUtil.splitNamespaces(name), obj);
    }

    public void set(String[] path, Object obj) {
        Context<Object, Object> container = create(path, null, path.length - 1);
        Object org = container.get(path[path.length - 1]);
        Class<?> objClass = obj.getClass(), orgClass = org != null ? org.getClass() : null;
        boolean c = org == null || org.equals(obj) || orgClass.isAssignableFrom(objClass);
        Assert.isTrue(c, c || objClass.isAssignableFrom(orgClass) ? null : "Cannot overwrite directory named '" + StringUtil.join(path, ".") + "' to " + obj);
        if(c)
            container.set(path[path.length - 1], obj);
    }

    public Context<Object, Object> create(String[] path, Document desc, int depth) {
        Context<Object, Object> context = entries;
        int i;
        for(i = 0; i < depth; i++)
            if(StringUtil.isNotEmpty(path[i])) {
                Object object = context.get(path[i]);
                if(object == null) {
                    object = createNamingDirectoryContainer(i == path.length - 1 ? desc : null);
                    context.set(path[i], object);
                }
                if(object instanceof Context)
                    context = (Context<Object, Object>) object;
                else
                    return null;
            }
        return context;
    }

    public void firstBuilt() {
        entries = new MapContext<Object>();
    }

    private Context<Object, Object> createNamingDirectoryContainer(Document desc) {
        String className = desc != null ? Wikiup.getCsidAttribute(desc, null) : null;
        Context<Object, Object> context = className != null ? Bootstrap.getInstance().build(Context.class, desc) : new DefaultWikiupNamingDirectory();
        if(!Interfaces.initialize(context, desc) && desc != null)
            Wikiup.getInstance().loadBeans(Object.class, context, desc);
        return context;
    }

    public Iterator<String> iterator() {
        return entries.getMap().keySet().iterator();
    }
}
