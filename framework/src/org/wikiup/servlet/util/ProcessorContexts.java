package org.wikiup.servlet.util;

import org.wikiup.Wikiup;
import org.wikiup.core.bean.WikiupTypeTranslator;
import org.wikiup.core.impl.beancontainer.BeanContainerByTranslator;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.inf.ProcessorContext;

public class ProcessorContexts {
    private static final WikiupTypeTranslator TRANSLATOR = Wikiup.getModel(WikiupTypeTranslator.class); 
    
    public static Object get(ProcessorContext context, String name, Dictionary<?> params) {
        ProcessorContext.ByParameters byParameters = Interfaces.unwrap(ProcessorContext.ByParameters.class, context);
        return byParameters != null ? byParameters.get(name, params) : context.get(name);
    }
    
    public static BeanContainer getBeanContainer(ProcessorContext context, String name, Dictionary<?> params) {
        return toBeanContainer(get(context, name, params));
    }

    public static BeanContainer toBeanContainer(Object obj) {
        if(obj == null)
            return null;
        BeanContainer beanContainer = Interfaces.unwrap(BeanContainer.class, obj);
        return beanContainer != null ? beanContainer : new BeanContainerByTranslator(obj, TRANSLATOR);
    }
}
