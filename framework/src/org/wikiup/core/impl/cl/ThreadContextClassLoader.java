package org.wikiup.core.impl.cl;

import org.wikiup.core.inf.ext.ClassDictionary;

public class ThreadContextClassLoader implements ClassDictionary {
    public Class get(String name) {
        try {
            return name != null ? Thread.currentThread().getContextClassLoader().loadClass(name) : null;
        } catch(ClassNotFoundException e) {
            return null;
        }
    }
}
