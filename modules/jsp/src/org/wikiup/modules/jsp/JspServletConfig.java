package org.wikiup.modules.jsp;

import org.wikiup.Wikiup;
import org.wikiup.framework.bean.WikiupNamingDirectory;
import org.wikiup.core.impl.context.MapContext;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.servlet.beans.ServletContextContainer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;

public class JspServletConfig extends MapContext<String> implements ServletConfig, Iterable<String>, DocumentAware {
    public String getServletName() {
        return "jsp-servlet";
    }

    public ServletContext getServletContext() {
        return Wikiup.getModel(ServletContextContainer.class).getServletContext();
    }

    public String getInitParameter(String s) {
        return get(s);
    }

    public Enumeration getInitParameterNames() {
        return Collections.enumeration(getMap().keySet());
    }

    public void aware(Document desc) {
        Dictionaries.setProperties(desc, this, WikiupNamingDirectory.getInstance());
    }
}
