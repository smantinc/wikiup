package org.wikiup.servlet.impl.tp;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.TagProcessor;
import org.wikiup.servlet.ms.ProcessorContextModelContainer;

import java.io.StringWriter;
import java.util.Iterator;

public class ForEachTagProcessor implements TagProcessor {

    public void process(ServletProcessorContext context, TagProcessor parent, String body, Getter<?> parameters, StringWriter writer) {
        BeanFactory modelProvider;
        String in = ValueUtil.toString(parameters.get("in"));
        String var = ValueUtil.toString(parameters.get("var"));
        ProcessorContextModelContainer container = context.pushModelContainer();
        if(in != null) {
            modelProvider = context.getModelContainer(StringUtil.evaluateEL(in, context), parameters);
            container.setModelContainer(modelProvider);
        }
        Iterator<BeanFactory> iterator = context.getModelContainerStack().getIteratorFromContextStack(null);
        while(iterator.hasNext())
            if((modelProvider = iterator.next()) != null) {
                if(var != null)
                    context.set(var, modelProvider.query(Object.class));
                context.pushModelContainer(modelProvider);
                parent.process(context, parent, body, parameters, writer);
                context.popModelContainer();
            }
        context.popModelContainer();
    }
}
