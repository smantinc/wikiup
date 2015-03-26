package org.wikiup.framework.impl.cl;

import org.wikiup.core.inf.ext.ClassDictionary;

public class JarArchiveClassLoader extends AbstractJarArchiveLoader implements ClassDictionary {
    public Class get(String name) {
        try {
            return name != null ? classLoader.loadClass(name) : null;
        } catch(ClassNotFoundException e) {
            return null;
        }
    }
}