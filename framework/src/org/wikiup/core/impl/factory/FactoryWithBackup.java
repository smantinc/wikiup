package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;

public class FactoryWithBackup<T> extends WrapperImpl<Factory<T>> implements Factory<T> {
    private Factory<T> backup;

    public FactoryWithBackup(Factory<T> wrapped, Factory<T> backup) {
        super(wrapped);
        this.backup = backup;
    }

    @Override
    public T build(Document doc) {
        T obj = wrapped.build(doc);
        return obj != null ? obj : backup.build(doc);
    }
}
