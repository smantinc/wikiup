package org.wikiup.servlet.inf.ext;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorModelContainer;
import org.wikiup.servlet.inf.ServletProcessorContextAware;


public abstract class ContextMethodSupport implements ProcessorModelContainer, ServletProcessorContextAware {
    private ServletProcessorContext context;

    public BeanContainer getModelContainer(String name, Dictionary<?> params) {
        return Interfaces.getModelContainer(invoke(context, params));
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    protected abstract Object invoke(ServletProcessorContext context, Dictionary<?> params);

    protected Object getParameter(Dictionary<?> params, String name, Object def) {
        Object obj = params.get(name);
        return obj != null ? obj : def;
    }

    protected String getStringParameter(Dictionary<?> params, String name, String def) {
        return ValueUtil.toString(getParameter(params, name, null), def);
    }

    protected int getIntegerParameter(Dictionary<?> params, String name, int def) {
        return ValueUtil.toInteger(ContextUtil.getString(params, name, null), def);
    }

    protected boolean getBooleanParameter(Dictionary<?> params, String name, boolean def) {
        return ValueUtil.toBoolean(ContextUtil.getString(params, name, null), def);
    }
}
