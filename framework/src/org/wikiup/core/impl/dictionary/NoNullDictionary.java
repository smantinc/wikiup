package org.wikiup.core.impl.dictionary;

import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Assert;

public class NoNullDictionary<T> extends WrapperImpl<Dictionary<T>> implements Dictionary<T> {
    public NoNullDictionary(Dictionary<T> wrapped) {
        super(wrapped);
    }

    @Override
    public T get(String name) {
        T obj = wrapped.get(name);
        Assert.notNull(obj, AttributeException.class, wrapped, name);
        return obj;
    }
}
