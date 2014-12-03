package org.wikiup.core.impl.cl;

import org.wikiup.core.inf.ext.ClassLoader;

public class ThreadContextClassLoader implements ClassLoader {
    public Class get(String name) {
        try {
            return name != null ? Thread.currentThread().getContextClassLoader().loadClass(name) : null;
        } catch(ClassNotFoundException e) {
            return null;
        }
    }
}
