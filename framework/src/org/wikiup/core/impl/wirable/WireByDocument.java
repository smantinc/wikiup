package org.wikiup.core.impl.wirable;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.Wirable;

public class WireByDocument<T extends DocumentAware> extends WrapperImpl<T> implements Wirable<T, Document> {
    public WireByDocument(T wrapped) {
        super(wrapped);
    }

    @Override
    public T wire(Document desc) {
        wrapped.aware(desc);
        return wrapped;
    }
}
