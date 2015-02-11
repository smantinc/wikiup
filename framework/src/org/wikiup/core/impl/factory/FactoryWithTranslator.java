package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Translator;

public class FactoryWithTranslator<T, P> extends WrapperImpl<Factory<T, P>> implements Factory<T, P> {
    private Translator<T, T> translator;

    public FactoryWithTranslator(Factory<T, P> wrapped, Translator<T, T> translator) {
        super(wrapped);
        this.translator = translator;
    }

    @Override
    public T build(P params) {
        return translator.translate(wrapped.build(params));
    }
}
