package org.wikiup.framework.impl.mf;

import org.wikiup.core.Wikiup;
import org.wikiup.framework.bean.WikiupNamingDirectory;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.core.util.Interfaces;

@Deprecated
public class WndiFactory implements ModelFactory {
    private WikiupNamingDirectory directory;

    public WndiFactory() {
        directory = Wikiup.getModel(WikiupNamingDirectory.class);
    }

    public BeanContainer get(String name) {
        return new WndiModelProvider(name);
    }

    private class WndiModelProvider implements BeanContainer {
        private String name;

        public WndiModelProvider(String name) {
            this.name = name;
        }

        public <E> E query(Class<E> clazz) {
            Object obj = directory.get(name);
            return Interfaces.cast(clazz, obj);
        }
    }
}
