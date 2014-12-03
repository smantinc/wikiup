package org.wikiup.core.impl.cl;

import org.wikiup.core.inf.ext.ClassLoader;

public class DefaultClassLoader implements ClassLoader {
    public Class get(String name) {
        try {
            return name != null ? Class.forName(name) : null;
        } catch(ClassNotFoundException e) {
            return null;
        }
    }
}
