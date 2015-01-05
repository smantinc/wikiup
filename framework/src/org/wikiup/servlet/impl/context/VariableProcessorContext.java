package org.wikiup.servlet.impl.context;

import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.context.MapContext;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class VariableProcessorContext implements ProcessorContext, ServletProcessorContextAware, DocumentAware, Context<Object, Object> {
    private Context<Object, Object> variables;
    private ServletProcessorContext context;

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    public BeanFactory getModelContainer(String name, Getter<?> params) {
        return Interfaces.cast(BeanFactory.class, variables.get(name));
    }

    public Object get(String name) {
        return variables.get(name);
    }

    public void set(String name, Object value) {
        variables.set(name, value);
    }

    public void aware(Document desc) {
        String scope = Documents.getDocumentValue(desc, "scope", null);
        if(scope == null)
            variables = new MapContext<Object>();
        else {
            Context<Object, Object> var = VariableDynamicSingleton.getInstance().get(scope);
            variables = var != null ? var : Null.getInstance();
            context.awaredBy(variables);
        }
        context.setProperties(desc, variables);
    }

    private static class RequestAttributeContext implements Context<Object, Object>, ServletProcessorContextAware {
        private HttpServletRequest request;

        public void setRequest(HttpServletRequest request) {
            this.request = request;
        }

        public Object get(String name) {
            return request != null ? request.getAttribute(name) : null;
        }

        public void set(String name, Object obj) {
            request.setAttribute(name, obj);
        }

        public void setServletProcessorContext(ServletProcessorContext context) {
            request = context.getServletRequest();
        }
    }

    private static class SessionAttributeContext implements Context<Object, Object>, ServletProcessorContextAware {
        private HttpSession session;

        public void setSession(HttpSession session) {
            this.session = session;
        }

        public Object get(String name) {
            Object attr = session != null ? session.getAttribute(name) : null;
            return attr != null ? attr : ContextUtil.getBeanProperty(session, name);
        }

        public void set(String name, Object obj) {
            session.setAttribute(name, obj);
        }

        public void setServletProcessorContext(ServletProcessorContext context) {
            session = context.getSession();
        }
    }

    private static class CookieContext implements Context<Object, Object>, ServletProcessorContextAware {
        private ServletProcessorContext context;

        public Object get(String name) {
            Cookie cookie = findRequestCookie(name);
            return cookie != null ? cookie.getValue() : null;
        }

        public void set(String name, Object value) {
            context.getServletResponse().addCookie(new Cookie(name, value.toString()));
        }

        private Cookie findRequestCookie(String name) {
            Cookie cookies[] = context.getServletRequest().getCookies();
            int i;
            if(cookies != null)
                for(i = 0; i < cookies.length; i++)
                    if(cookies[i].getName().equals(name))
                        return cookies[i];
            return null;
        }

        public void setServletProcessorContext(ServletProcessorContext context) {
            this.context = context;
        }
    }

    public static class VariableDynamicSingleton extends WikiupDynamicSingleton<VariableDynamicSingleton> implements Context<Context<Object, Object>, Context<Object, Object>>, Releasable {
        private Map<String, ThreadLocal<Context<Object, Object>>> threadLocals;

        public VariableDynamicSingleton() {
            set("cookie", new CookieContext());
            set("threadlocal", new MapContext<Object>());
            set("request", new RequestAttributeContext());
            set("session", new SessionAttributeContext());
        }

        public void firstBuilt() {
            threadLocals = new HashMap<String, ThreadLocal<Context<Object, Object>>>();
        }

        static public VariableDynamicSingleton getInstance() {
            return getInstance(VariableDynamicSingleton.class);
        }

        public Context<Object, Object> get(String name) {
            ThreadLocal<Context<Object, Object>> tl = threadLocals.get(name);
            return tl != null ? tl.get() : null;
        }

        public void set(String name, final Context<Object, Object> obj) {
            ThreadLocal<Context<Object, Object>> tl = new ThreadLocal<Context<Object, Object>>() {
                @Override
                protected Context<Object, Object> initialValue() {
                    return obj;
                }
            };
            threadLocals.put(name, tl);
        }

        public void release() {
            for(ThreadLocal<?> tl : threadLocals.values())
                tl.remove();
            threadLocals.clear();
        }
    }
}
