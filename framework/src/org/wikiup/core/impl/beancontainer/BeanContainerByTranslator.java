package org.wikiup.core.impl.beancontainer;

import java.util.HashMap;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Translator;
import org.wikiup.core.util.Interfaces;

public class BeanContainerByTranslator extends WrapperImpl<Object> implements BeanContainer {
    private HashMap<Class<?>, Object> byClasses = new HashMap<Class<?>, Object>();
    private Translator<Object, Object> translator;

    public BeanContainerByTranslator(Object wrapped, Translator<Object, Object> translator) {
        super(wrapped);
        this.translator = translator;
    }

    @Override
    public <T> T query(Class<T> clazz) {
        Object inst = Interfaces.unwrap(clazz, wrapped);
        if(inst == null) {
            if(byClasses.containsKey(clazz))
                return Interfaces.cast(clazz, byClasses.get(clazz));
            inst = translator.translate(wrapped);
            byClasses.put(clazz, inst);
        }
        return Interfaces.cast(clazz, inst);
    }
}
