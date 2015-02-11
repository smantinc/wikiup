package org.wikiup.core.impl.factory;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Factory;

public class FactoryWithBackup<T, P> extends WrapperImpl<Factory<T, P>> implements Factory<T, P> {
    private Factory<T, P> backup;

    public FactoryWithBackup(Factory<T, P> wrapped, Factory<T, P> backup) {
        super(wrapped);
        this.backup = backup;
    }

    @Override
    public T build(P params) {
        T obj = wrapped.build(params);
        return obj != null ? obj : backup.build(params);
    }
}
