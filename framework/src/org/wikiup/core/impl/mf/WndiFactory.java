package org.wikiup.core.impl.mf;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupNamingDirectory;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.core.util.Interfaces;

public class WndiFactory implements ModelFactory {
    private WikiupNamingDirectory directory;

    public WndiFactory() {
        directory = Wikiup.getModel(WikiupNamingDirectory.class);
    }

    public ModelProvider get(String name) {
        return new WndiModelProvider(name);
    }

    private class WndiModelProvider implements ModelProvider {
        private String name;

        public WndiModelProvider(String name) {
            this.name = name;
        }

        public <E> E getModel(Class<E> clazz) {
            Object obj = directory.get(name);
            return Interfaces.cast(clazz, obj);
        }
    }
}
