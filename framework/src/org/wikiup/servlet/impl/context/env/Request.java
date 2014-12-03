package org.wikiup.servlet.impl.context.env;

import org.wikiup.core.impl.iterable.BeanPropertyNames;
import org.wikiup.core.impl.iterable.IterableCollection;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

public class Request implements Getter<Object>, ServletProcessorContextAware, Iterable<String> {
    private ServletProcessorContext context;

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    public Object get(String name) {
        Object obj = ContextUtil.getBeanProperty(this, name);
        return obj != null ? obj : ContextUtil.getBeanProperty(context.getServletRequest(), name);
    }

    public Iterator<String> iterator() {
        IterableCollection<String> ci = new IterableCollection<String>(new BeanPropertyNames(this.getClass()));
        ci.append(new BeanPropertyNames(HttpServletRequest.class));
        return ci.iterator();
    }

    public Getter<String> getHeaders() {
        return new Getter<String>() {
            public String get(String name) {
                return context.getHeader(name);
            }
        };
    }
}
