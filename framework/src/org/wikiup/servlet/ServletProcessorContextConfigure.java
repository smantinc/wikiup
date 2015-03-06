package org.wikiup.servlet;

import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Documents;
import org.wikiup.servlet.impl.mapping.ServletMapping;

import java.util.HashMap;
import java.util.Map;

public class ServletProcessorContextConfigure extends WikiupDynamicSingleton<ServletProcessorContextConfigure> implements DocumentAware, Dictionary<Object> {
    private Map<String, ServletMapping> mapping;

    static public ServletProcessorContextConfigure getInstance() {
        return getInstance(ServletProcessorContextConfigure.class);
    }

    synchronized public Document mapServletConfigure(ServletProcessorContext context) {
        ServletMapping map = getServletConfigure();
        Document configure = map != null ? map.map(context) : null;
        if(configure == null) {
            map = mapping.get("default-servlet");
            configure = map != null ? map.map(context) : null;
        }
        return configure;
    }

    synchronized public ServletMapping getRequestConfigure() {
        return mapping.get("request");
    }

    synchronized public ServletMapping getServletConfigure() {
        return mapping.get("servlet");
    }

    public void firstBuilt() {
        mapping = new HashMap<String, ServletMapping>();
    }

    @Override
    public void aware(Document desc) {
        for(Document node : desc.getChildren()) {
            String name = Documents.getId(node);
            mapping.put(name, new ServletMapping(name, node));
        }
    }

    public Object get(String name) {
        return mapping.get(name);
    }
}
