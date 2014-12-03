package org.wikiup.modules.groovy;

import groovy.lang.GroovyShell;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class GroovyShellPool extends WikiupDynamicSingleton<GroovyShellPool> implements Getter<GroovyShell> {
    private static final String THREAD_LOCAL_NAMESPACE = "threadlocal";

    private GroovyShellContainer container;
    private ThreadLocalJythonInterpreterContainer threadLocalPythonInterpreter;

    static public GroovyShellPool getInstance() {
        return getInstance(GroovyShellPool.class);
    }

//  public void cloneFrom(GroovyShellPool instance)
//  {
//    threadLocalPythonInterpreter = instance.threadLocalPythonInterpreter;
//    container = instance.container;
//  }

    public void firstBuilt() {
        threadLocalPythonInterpreter = new ThreadLocalJythonInterpreterContainer();
    }

    public GroovyShell get(String name) {
        int idx = StringUtil.isEmpty(name) ? -1 : name.indexOf(':');
        if(idx != -1) {
            String ns = name.substring(0, idx);
            String key = name.substring(idx + 1);
            if(ns.equalsIgnoreCase(THREAD_LOCAL_NAMESPACE))
                return threadLocalPythonInterpreter.get().get(key);
            return getContainer().get(key);
        }
        return new GroovyShell();
    }

    public void release(String name) {
        int idx = StringUtil.isEmpty(name) ? -1 : name.indexOf(':');
        if(idx != -1) {
            String ns = name.substring(0, idx);
            String key = name.substring(idx + 1);
            if(ns.equalsIgnoreCase(THREAD_LOCAL_NAMESPACE))
                threadLocalPythonInterpreter.get().release(key);
            else
                getContainer().release(key);
        }
    }

    private GroovyShellContainer getContainer() {
        return container != null ? container : (container = new GroovyShellContainer());
    }

    static private class GroovyShellContainer implements Getter<GroovyShell>, Releasable {
        private Map<String, GroovyShell> container;

        public GroovyShell get(String name) {
            GroovyShell shell = getContainer().get(name);
            if(shell == null)
                getContainer().put(name, shell = new GroovyShell());
            return shell;
        }

        public void release(String name) {
            container.remove(name);
        }

        public void release() {
            getContainer().clear();
        }

        private Map<String, GroovyShell> getContainer() {
            return container != null ? container : (container = new HashMap<String, GroovyShell>());
        }
    }

    static private class ThreadLocalJythonInterpreterContainer extends ThreadLocal<GroovyShellContainer> {
        @Override
        protected GroovyShellContainer initialValue() {
            return new GroovyShellContainer();
        }
    }
}
