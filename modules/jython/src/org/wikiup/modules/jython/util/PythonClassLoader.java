package org.wikiup.modules.jython.util;

import org.wikiup.Wikiup;
import org.wikiup.core.bean.WikiupClassLoader;

public class PythonClassLoader extends ClassLoader {
    public PythonClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return Wikiup.getModel(WikiupClassLoader.class).get(name);
        } catch(Exception e) {
            return null;
        }
    }
}
