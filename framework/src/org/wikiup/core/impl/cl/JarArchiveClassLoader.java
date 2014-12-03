package org.wikiup.core.impl.cl;

import org.wikiup.core.inf.ext.ClassLoader;

public class JarArchiveClassLoader extends AbstractJarArchiveLoader implements ClassLoader {
    public Class get(String name) {
        try {
            return name != null ? classLoader.loadClass(name) : null;
        } catch(ClassNotFoundException e) {
            return null;
        }
    }
}