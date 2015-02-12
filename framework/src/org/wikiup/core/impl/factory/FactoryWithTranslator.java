package org.wikiup.core.impl.factory;

import org.wikiup.core.Constants;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Decorator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Translator;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

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

    public static final class DECORATOR<T, P> implements Decorator<Factory<T, P>> {
        @Override
        public Factory<T, P> decorate(Factory<T, P> factory, Document desc) {
            String className = Documents.ensureAttributeValue(desc, Constants.Attributes.TRANSLATOR);
            Class<Translator<T, T>> clazz = Interfaces.getClass(className);
            return new FactoryWithTranslator<T, P>(factory, Interfaces.newInstance(clazz));
        }
    }
}
