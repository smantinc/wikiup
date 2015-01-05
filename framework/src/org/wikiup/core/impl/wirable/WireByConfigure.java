package org.wikiup.core.impl.wirable;

import org.wikiup.core.impl.setter.BeanPropertySetter;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Wirable;
import org.wikiup.core.util.ContextUtil;

public class WireByConfigure<T> extends WrapperImpl<T> implements Wirable<T, Getter<?>> {
    private Document configure;
    
    public WireByConfigure(T wrapped, Document configure) {
        super(wrapped);
        this.configure = configure;
    }

    @Override
    public T wire(Getter<?> getter) {
        if(configure != null)
            ContextUtil.setProperties(configure, new BeanPropertySetter(wrapped), getter);
        return wrapped;
    }
}
