package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.document.MergedDocument;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Decorator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;

public class FactoryByMergedDocument<T> extends WrapperImpl<Factory<T>> implements Factory<T> {
    private Document merging;
    
    public FactoryByMergedDocument(Factory<T> wrapped, Document merging) {
        super(wrapped);
        this.merging = merging;
    }

    @Override
    public T build(Document desc) {
        return wrapped.build(new MergedDocument(desc, merging));
    }
    
    public static final class DECORATOR<T> implements Decorator<Factory<T>> {
        @Override
        public Factory<T> decorate(Factory<T> obj, Document desc) {
            return new FactoryByMergedDocument<T>(obj, desc);
        }
    }
}
