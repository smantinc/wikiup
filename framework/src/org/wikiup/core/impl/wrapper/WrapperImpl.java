package org.wikiup.core.impl.wrapper;

import org.wikiup.core.inf.Wrapper;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;

public class WrapperImpl<T> implements Wrapper<T> {
    protected T wrapped;

    public WrapperImpl(T wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public T wrapped() {
        return wrapped;
    }

    public void wrap(T obj) {
        wrapped = obj;
    }

    public T unwrap() {
        return Interfaces.unwrap(wrapped);
    }

    public void link(WrapperImpl<T> wrapper) {
        if(Interfaces.unwrap(wrapped, wrapper) == null) {
            try {
                T w = (T) wrapper;
                wrapper.wrap(wrapped);
                wrapped = w;
            }
            catch(ClassCastException e) {
                Assert.fail(new IllegalArgumentException("Linking object must be implementation of <T>"));
            }
        }
    }

    public void unlink(WrapperImpl<T> wrapper) {
        WrapperImpl<T> l = Interfaces.cast(WrapperImpl.class, wrapped);
        while(l != null) {
            if(l.wrapped() == wrapper) {
                l.wrap(wrapper.wrapped);
                break;
            }
            l = Interfaces.cast(WrapperImpl.class, l.wrapped());
        }
    }

    public <E extends T> E unwrap(Class<E> clazz) {
        return Interfaces.unwrap(clazz, wrapped);
    }
}
