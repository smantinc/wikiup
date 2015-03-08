package org.wikiup.core.impl.wirable;

import org.wikiup.core.impl.setter.BeanPropertySetter;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Dictionaries;

public class WireByConfigure<T> extends WrapperImpl<T> implements Wirable<T, Dictionary<?>> {
    private Document configure;
    
    public WireByConfigure(T wrapped, Document configure) {
        super(wrapped);
        this.configure = configure;
    }

    @Override
    public T wire(Dictionary<?> dictionary) {
        if(configure != null)
            Dictionaries.setProperties(configure, new BeanPropertySetter(wrapped), dictionary);
        return wrapped;
    }
}
