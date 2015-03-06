package org.wikiup.servlet.impl.context.method;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ext.ContextMethodSupport;

public class Sequence extends ContextMethodSupport {
    private static final String SEQ_ATTRIBUTE_NAME = "_wk_method_seq";

    public Object invoke(ServletProcessorContext context, Dictionary<?> params) {
        String initTick = getStringParameter(params, "init-seq", "0");
        int tick = ValueUtil.toInteger(context.getAttribute(SEQ_ATTRIBUTE_NAME), 0);
        int step = getIntegerParameter(params, "step", 1);
        if(StringUtil.isEmpty(initTick))
            return String.valueOf(tick += step);
        context.set(SEQ_ATTRIBUTE_NAME, ValueUtil.toInteger(initTick, 0));
        return null;
    }
}
