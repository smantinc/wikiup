package org.wikiup.servlet.beans;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import javax.servlet.ServletContext;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.impl.iterable.BeanPropertyNames;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;

public class ServletContextContainer extends WikiupDynamicSingleton<ServletContextContainer> implements Dictionary<Object>, Iterable<String> {
    private ServletContext servletContext;

    static public ServletContextContainer getInstance() {
        return getInstance(ServletContextContainer.class);
    }

    @Deprecated
    public String getRealPath(String path) {
        String p = servletContext.getRealPath(path.startsWith("/") ? path : '/' + path);
        try {
            return URLDecoder.decode(p, WikiupConfigure.CHAR_SET);
        } catch(UnsupportedEncodingException e) {
            return p;
        }
    }

    public Resource getResource(String uri) {
        return new ServletContextResource(StringUtil.connect("", uri, '/'));
    }

    public Dictionary<String> getRealPath() {
        return new Dictionary<String>() {
            public String get(String name) {
                return getRealPath(name);
            }
        };
    }

    public void firstBuilt() {
    }
    
    public void log(String category, Throwable ex) {
        servletContext.log(category, ex);
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public Object get(String name) {
        return Dictionaries.getBeanProperty(this, name);
    }

    public Iterator<String> iterator() {
        return new BeanPropertyNames(this.getClass(), true).iterator();
    }

    private class ServletContextResource implements Resource, BeanContainer {
        private String uri;

        private ServletContextResource(String uri) {
            this.uri = uri;
        }

        public String getHost() {
            return null;
        }

        public String getURI() {
            return uri;
        }

        public boolean exists() {
            return FileUtil.isExists(servletContext.getRealPath(uri));
        }

        public InputStream open() {
            return servletContext.getResourceAsStream(uri);
        }

        public <E> E query(Class<E> clazz) {
            Object obj = null;
            if(clazz.equals(File.class))
                obj = new File(servletContext.getRealPath(uri));
            return Interfaces.cast(clazz, obj);
        }
    }
}
