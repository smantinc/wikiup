package org.wikiup.servlet.impl.context;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.impl.mp.IterableModelProvider;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class ParameterProcessorContext implements ProcessorContext, ServletProcessorContextAware, Iterable<String> {
    private ServletProcessorContext context;

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    public BeanFactory getModelContainer(String name, Getter<?> params) {
        List<String> names = new ArrayList<String>();
        if("*".equals(name)) {
            String p = ValueUtil.toString(params.get("pattern"));
            Enumeration e = context.getServletRequest().getParameterNames();
            Pattern pattern = p != null ? Pattern.compile(p) : null;
            while(e.hasMoreElements()) {
                String paramName = (String) e.nextElement();
                if(pattern == null || pattern.matcher(paramName).matches())
                    names.add(paramName);
            }
        } else {
            String[] values = context.getServletRequest().getParameterValues(name);
            if(values != null)
                for(String value : values)
                    names.add(value);
        }
        return names.size() > 0 ? new IterableModelProvider(names) : null;
    }

    public Object get(String name) {
        if(name.length() == 1) {
            for(char d : WikiupConfigure.HANDLER_DELIMETERS)
                if(name.charAt(0) == d)
                    return context.getHandlerParameter(d);
        }
        return context.getParameter(name);
    }

    public Iterator<String> iterator() {
        return context.getServletRequest().getParameterMap().keySet().iterator();
    }
}
