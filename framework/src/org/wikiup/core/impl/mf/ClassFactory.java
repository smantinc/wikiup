package org.wikiup.core.impl.mf;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupClassLoader;
import org.wikiup.core.impl.mp.InstanceModelProvider;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.core.util.Assert;

@Deprecated
public class ClassFactory implements ModelFactory {
    private WikiupClassLoader cl;

    public ClassFactory() {
        cl = Wikiup.getModel(WikiupClassLoader.class);
    }

    public ClassFactory(WikiupClassLoader cl) {
        this.cl = cl;
    }

    public BeanContainer get(String className) {
        try {
            Class<?> clazz = cl != null ? cl.get(className) : Class.forName(className);
            Assert.notNull(clazz, ClassNotFoundException.class, className);
            return className != null ? new InstanceModelProvider(clazz.newInstance()) : null;
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        return null;
    }
}
