package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.util.Interfaces;

public class FactoryWrapper<T, P> extends WrapperImpl<Factory<T, P>> implements Factory<T, P> {
    public FactoryWrapper(Factory<T, P> wrapped) {
        super(wrapped);
    }

    @Override
    public T build(P params) {
        return wrapped.build(params);
    }

    public Factory<T, Class<?>> asByClass() {
        return Interfaces.cast(this);
    }

    public Factory<T, Document> asByDocument() {
        return Interfaces.cast(this);
    }

    public Factory<T, String> asByName() {
        return Interfaces.cast(this);
    }
}
