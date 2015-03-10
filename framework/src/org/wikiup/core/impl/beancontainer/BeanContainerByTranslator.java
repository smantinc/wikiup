package org.wikiup.core.impl.beancontainer;

import java.util.HashMap;

import org.wikiup.core.bean.WikiupTypeTranslator;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Interfaces;

public class BeanContainerByTranslator extends WrapperImpl<Object> implements BeanContainer {
    private HashMap<Class<?>, Object> byClasses = new HashMap<Class<?>, Object>();
    private WikiupTypeTranslator translator;

    public BeanContainerByTranslator(Object obj, WikiupTypeTranslator translator) {
        super(obj);
        this.translator = translator;
    }

    @Override
    public <T> T query(Class<T> clazz) {
        Object inst = Interfaces.unwrap(clazz, wrapped);
        if(inst == null) {
            if(byClasses.containsKey(clazz))
                return Interfaces.cast(clazz, byClasses.get(clazz));
            inst = translator.cast(clazz, wrapped);
            byClasses.put(clazz, inst);
        }
        return Interfaces.cast(clazz, inst);
    }
}
