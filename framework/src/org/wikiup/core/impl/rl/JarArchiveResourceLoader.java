package org.wikiup.core.impl.rl;

import org.wikiup.core.impl.cl.AbstractJarArchiveLoader;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.inf.ext.ResourceLoader;
import org.wikiup.core.util.Resources;

public class JarArchiveResourceLoader extends AbstractJarArchiveLoader implements ResourceLoader {
    public Iterable<Resource> get(String name) {
        return Resources.getClassPathResources(name, classLoader);
    }

}
