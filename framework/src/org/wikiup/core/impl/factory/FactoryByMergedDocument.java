package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.document.MergedDocument;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Decorator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;

public class FactoryByMergedDocument<T> extends WrapperImpl<Factory.ByDocument<T>> implements Factory.ByDocument<T> {
    private Document merging;
    
    public FactoryByMergedDocument(ByDocument<T> wrapped, Document merging) {
        super(wrapped);
        this.merging = merging;
    }

    @Override
    public T build(Document desc) {
        return wrapped.build(new MergedDocument(desc, merging));
    }
    
    public static final class DECORATOR<T> implements Decorator<Factory.ByDocument<T>> {
        @Override
        public ByDocument<T> decorate(ByDocument<T> obj, Document desc) {
            return new FactoryByMergedDocument<T>(obj, desc);
        }
    }
}
