package org.wikiup.core.impl.factory;

import org.wikiup.core.Constants;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Decorator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Translator;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

public class FactoryWithTranslator<T> extends WrapperImpl<Factory<T>> implements Factory<T> {
    private Translator<T, T> translator;

    public FactoryWithTranslator(Factory<T> wrapped, Translator<T, T> translator) {
        super(wrapped);
        this.translator = translator;
    }

    @Override
    public T build(Document doc) {
        return translator.translate(wrapped.build(doc));
    }

    public static final class DECORATOR<T, P> implements Decorator<Factory<T>> {
        @Override
        public Factory<T> decorate(Factory<T> factory, Document desc) {
            String className = Documents.ensureAttributeValue(desc, Constants.Attributes.TRANSLATOR);
            Class<Translator<T, T>> clazz = Interfaces.getClass(className);
            return new FactoryWithTranslator<T>(factory, Interfaces.newInstance(clazz));
        }
    }
}
