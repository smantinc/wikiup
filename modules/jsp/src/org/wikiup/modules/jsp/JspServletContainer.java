package org.wikiup.modules.jsp;

import org.apache.jasper.servlet.JspServlet;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.impl.iterable.BeanPropertyNames;
import org.wikiup.core.inf.ExpressionLanguage;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.ContextUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Iterator;

public class JspServletContainer extends WikiupDynamicSingleton<JspServletContainer> implements Context<Object, Object>, Iterable<String>, Releasable {
    private JspServlet jspServlet;
    private ServletConfig servletConfig;
    private ExpressionLanguage<Getter<?>, String> el;

    public void firstBuilt() {
    }

    public Object get(String name) {
        return ContextUtil.getBeanProperty(this, name);
    }

    public void set(String name, Object obj) {
        ContextUtil.setBeanProperty(this, name, obj);
    }

    public Iterator<String> iterator() {
        return new BeanPropertyNames(getClass(), true).iterator();
    }

    public JspServlet getJspServlet() {
        return jspServlet;
    }

    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    public void setJspServlet(JspServlet jspServlet) throws ServletException {
        this.jspServlet = jspServlet;
        this.jspServlet.init(servletConfig);
    }

    public ExpressionLanguage<Getter<?>, String> getEl() {
        return el;
    }

    public void setEl(ExpressionLanguage<Getter<?>, String> el) {
        this.el = el;
    }

    public void release() {
        if(jspServlet != null)
            jspServlet.destroy();
        jspServlet = null;
    }
}