package org.wikiup.servlet.impl.context.env;

import org.wikiup.core.impl.getter.BeanPropertyGetter;
import org.wikiup.core.impl.iterable.BeanPropertyNames;
import org.wikiup.core.util.FileUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.util.Iterator;

public class Location extends BeanPropertyGetter implements Iterable<String>, ServletProcessorContextAware {
    protected ServletProcessorContext context;

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    public Iterator<String> iterator() {
        Iterable<String> iterable = new BeanPropertyNames(this.getClass(), true);
        return iterable.iterator();
    }

    @Override
    public String toString() {
        return context.getRequestURL();
    }

    public String getHostname() {
        return context.getServletRequest().getServerName();
    }

    public String getPathname() {
        return context.getRequestURI();
    }

    public String getFilename() {
        String ext = FileUtil.getFileExt(context.getRequestURI());
        return FileUtil.getFileName(context.getRequestPath(true), '/') + '.' + ext;
    }

    public String getParentname() {
        String uri = context.getRequestURI();
        int pos = uri.lastIndexOf('/');
        return pos != -1 ? uri.substring(0, pos) : "/";
    }

    public int getPort() {
        return context.getServletRequest().getLocalPort();
    }

    public String getProtocol() {
        return context.getServletRequest().getProtocol();
    }

    public String getSearch() {
        return context.getServletRequest().getQueryString();
    }
}
