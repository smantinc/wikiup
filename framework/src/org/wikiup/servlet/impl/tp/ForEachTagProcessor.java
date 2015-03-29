package org.wikiup.servlet.impl.tp;

import java.io.StringWriter;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.TagProcessor;
import org.wikiup.servlet.util.ProcessorContexts;

public class ForEachTagProcessor implements TagProcessor {
    public void process(ServletProcessorContext context, TagProcessor parent, String body, Dictionary<?> parameters, StringWriter writer) {
        String in = ValueUtil.toString(parameters.get("in"));
        String var = ValueUtil.toString(parameters.get("var"));
        ServletProcessorContext.BeanStack beanStack = context.getBeanStack();

        if(in != null) {
            Object ctx = ProcessorContexts.get(context, StringUtil.evaluateEL(in, context), parameters);
            beanStack.push(ctx);
        }
        Iterable<?> iterable = beanStack.peek(Iterable.class);
        Assert.notNull(iterable, IllegalStateException.class);
        for(Object obj : iterable)
            if(obj != null) {
                if(var != null)
                    context.set(var, obj);
                beanStack.push(obj);
                parent.process(context, parent, body, parameters, writer);
                beanStack.pop();
            }
        if(in != null)
            beanStack.pop();
    }
}
