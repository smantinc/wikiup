package org.wikiup.servlet.impl.action;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupModelProvider;
import org.wikiup.core.impl.bindable.ByPropertyAutomatically;
import org.wikiup.core.impl.context.BeanPropertyContext;
import org.wikiup.core.impl.context.MapContext;
import org.wikiup.core.impl.document.Context2Document;
import org.wikiup.core.impl.iterable.BeanPropertyNames;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ClassIdentity;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ServletActionSupport implements ServletAction {
    private Object instance;

    public ServletActionSupport() {
        instance = this;
    }

    public ServletActionSupport(Object instance) {
        this.instance = instance;
    }

    public void doAction(ServletProcessorContext context, Document doc) {
        String entry = context.getContextAttribute(doc, "entry", context.getHandlerParameter('!'));
        String handler = entry != null ? ContextUtil.getPropertyName(entry, false) : null;
        if(handler != null)
            try {
                ByPropertyAutomatically bindable = new ByPropertyAutomatically(new WikiupBeanGetter(), Wikiup.getModel(WikiupModelProvider.class));
                Interfaces.initialize(bindable, doc);
                bindable.bind(instance);

                Iterator<Document> iterator = doc.getChild(handler) != null ? doc.getChildren(handler).iterator() : doc.getChildren(entry).iterator();
                if(!iterator.hasNext())
                    invokeHandler(handler, context, doc);
                else
                    while(iterator.hasNext())
                        invokeHandler(handler, context, iterator.next());
            } catch(InvocationTargetException e) {
                Assert.fail(e);
            } catch(IllegalAccessException e) {
                Assert.fail(e);
            }
    }

    private void invokeHandler(String handlerName, ServletProcessorContext context, Document doc) throws InvocationTargetException, IllegalAccessException {
        SupportedHandler handler = getSupportedHandler(handlerName, context, doc);
        if(handler != null) {
            Object ret = handler.invoke();
            Map<String, Object> mapModel = Interfaces.cast(Map.class, ret);
            if(mapModel != null)
                context.getResponseBuffer().setDocument(new Context2Document(new MapContext<Object>(mapModel), mapModel.keySet()));
            else {
                Document node = Interfaces.cast(Document.class, ret);
                if(node != null)
                    context.getResponseBuffer().setDocument(node);
                else if(ret instanceof String)
                    context.getResponseWriter().write((String) ret);
                else if(ret != null)
                    context.getResponseBuffer().setDocument(new Context2Document(new BeanPropertyContext(ret), new BeanPropertyNames(ret.getClass())));
            }
        }
    }


    private SupportedHandler getSupportedHandler(String handler, ServletProcessorContext context, Document desc) {
        BeanFactory mc = context.getModelContainer();
        for(Method m : instance.getClass().getMethods()) {
            if(m.getName().equals(handler) || m.getName().equals(ContextUtil.getPropertyName("do-" + handler, false))) {
                List<Object> parameters = new ArrayList<Object>();
                for(Class<?> argClass : m.getParameterTypes()) {
                    Object param;
                    if(argClass.isInstance(desc))
                        parameters.add(desc);
                    else if((param = mc.query(argClass)) != null)
                        parameters.add(param);
                    else {
                        parameters = null;
                        break;
                    }
                }
                if(parameters != null)
                    return new SupportedHandler(m, parameters.toArray());
            }
        }
        return null;
    }

    private class SupportedHandler {
        private Method method;
        private Object[] parameters;

        private SupportedHandler(Method method, Object[] parameters) {
            this.method = method;
            this.parameters = parameters;
        }

        private Object invoke() throws InvocationTargetException, IllegalAccessException {
            return method.invoke(instance, parameters);
        }
    }

    private static class WikiupBeanGetter implements Getter<Object> {
        public Object get(String name) {
            ClassIdentity csid = new ClassIdentity(name);
            try {
                return csid.getBean(Object.class);
            } catch(Exception e) {
                return null;
            }
        }
    }
}
