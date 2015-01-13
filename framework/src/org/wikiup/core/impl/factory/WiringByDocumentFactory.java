package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Decorator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Interfaces;

public class WiringByDocumentFactory<T> extends WrapperImpl<Factory.ByDocument<T>> implements Factory.ByDocument<T> {
    public WiringByDocumentFactory(Factory.ByDocument<T> wrapped) {
        super(wrapped);
    }

    @Override
    public T build(Document desc) {
        T bean = wrapped.build(desc);
        Wirable.ByDocument<T> wirable = Interfaces.cast(Wirable.ByDocument.class, bean);
        return wirable != null ? wirable.wire(desc) : bean;
    }

    public static final class DECORATOR<T> implements Decorator<ByDocument<T>> {
        @Override
        public ByDocument<T> decorate(ByDocument<T> factory, Document desc) {
            return new WiringByDocumentFactory<T>(factory);
        }
    }
}
