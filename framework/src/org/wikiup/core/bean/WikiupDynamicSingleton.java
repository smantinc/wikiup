package org.wikiup.core.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Setter;
import org.wikiup.core.util.Interfaces;

public abstract class WikiupDynamicSingleton<E extends WikiupDynamicSingleton> implements DocumentAware {
    abstract public void firstBuilt();

    protected WrapperImpl<WikiupDynamicSingleton> instanceContainer;

    protected WikiupDynamicSingleton() {
        Class<E> clazz = (Class<E>) this.getClass();
        Object inst = WikiupDynamicSingletons.getInstance().put(this);
        if(inst != null) {
            E singleton = clazz.cast(inst);
            cloneFrom(singleton);
            instanceContainer = singleton.instanceContainer;
            instanceContainer.wrap(this);
        } else {
            instanceContainer = new WrapperImpl<WikiupDynamicSingleton>(this);
            firstBuilt();
        }
    }

    public WrapperImpl<WikiupDynamicSingleton> getInstanceContainer() {
        return instanceContainer;
    }

    public void aware(Document desc) {
        Setter<Object> setter = Interfaces.cast(Setter.class, this);
        if(setter != null)
            Wikiup.getInstance().loadBeans(Object.class, setter, desc);
    }

    @Deprecated
    synchronized static protected <E> E getInstance(Class<E> clazz) {
        return WikiupDynamicSingletons.getInstance().query(clazz);
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
}