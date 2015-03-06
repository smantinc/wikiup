package org.wikiup.servlet.impl.context.method;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ext.ContextMethodSupport;


public class Assign extends ContextMethodSupport {
    public Object invoke(ServletProcessorContext context, Dictionary<?> params) {
        context.set(getStringParameter(params, "name", null), getParameter(params, "value", null));
        return null;
    }
}
