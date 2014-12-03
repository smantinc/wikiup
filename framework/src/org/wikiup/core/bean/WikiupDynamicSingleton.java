package org.wikiup.core.bean;

import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Setter;
import org.wikiup.core.inf.ext.Container;
import org.wikiup.core.util.Interfaces;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class WikiupDynamicSingleton<E extends WikiupDynamicSingleton> implements DocumentAware {
    abstract public void firstBuilt();

    protected WikiupDynamicSingletonContainer instanceContainer;

    protected WikiupDynamicSingleton() {
        Class<E> clazz = (Class<E>) this.getClass();
        Object inst = WikiupDynamicSingletons.getInstance().put(this);
        if(inst != null) {
            E singleton = clazz.cast(inst);
            cloneFrom(singleton);
            instanceContainer = singleton.instanceContainer;
            instanceContainer.put(this);
        } else {
            instanceContainer = new WikiupDynamicSingletonContainer(this);
            firstBuilt();
        }
    }

    public WikiupDynamicSingletonContainer getInstanceContainer() {
        return instanceContainer;
    }

    public void aware(Document desc) {
        Setter<Object> setter = Interfaces.cast(Setter.class, this);
        if(setter != null)
            Wikiup.getInstance().loadBeans(Object.class, setter, desc);
    }

    @Deprecated
    synchronized static protected <E> E getInstance(Class<E> clazz) {
        return WikiupDynamicSingletons.getInstance().getModel(clazz);
    }

    public void cloneFrom(E instance) {
        cloneFrom(instance, this.getClass());
    }

    protected void cloneFrom(Object instance, Class<?> clazz) {
        for(Field field : clazz.getDeclaredFields()) {
            try {
                int modifiers = field.getModifiers();
                if(!(Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers))) {
                    if(!field.isAccessible())
                        field.setAccessible(true);
                    field.set(this, field.get(instance));
                }
            } catch(IllegalAccessException e) {
            }
        }
    }

    static public class WikiupDynamicSingletonContainer implements Container<WikiupDynamicSingleton> {
        private WikiupDynamicSingleton instance;

        public WikiupDynamicSingletonContainer(WikiupDynamicSingleton instance) {
            put(instance);
        }

        public WikiupDynamicSingleton get() {
            return instance;
        }

        public void put(WikiupDynamicSingleton instance) {
            this.instance = instance;
        }
    }
}