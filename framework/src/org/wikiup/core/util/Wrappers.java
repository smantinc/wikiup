package org.wikiup.core.util;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Expirable;
import org.wikiup.core.inf.Wrapper;

public class Wrappers {

    public static <T> T unwrap(T wrapper) {
        while(wrapper instanceof Wrapper)
            wrapper = ((Wrapper<T>) wrapper).wrapped();
        return wrapper;
    }

    public static <T> T unwrap(Class<T> clazz, Object wrapper) {
        do {
            if(clazz.isInstance(wrapper))
                return clazz.cast(wrapper);
            Wrapper<?> w = Interfaces.cast(Wrapper.class, wrapper);
            wrapper = w != null ? w.wrapped() : null;
        } while(wrapper != null);
        return null;
    }

    public static <T> T unwrap(Object wrapper, T instance) {
        do {
            if(wrapper == instance)
                return instance;
            Wrapper<?> w = Interfaces.cast(Wrapper.class, wrapper);
            wrapper = w != null ? w.wrapped() : null;
        } while(wrapper != null);
        return null;
    }

    public static Expirable onExpired(Expirable expirable, Runnable runnable) {
        return new OnExpiredRunnable(expirable, runnable);
    }

    private static class OnExpiredRunnable extends WrapperImpl<Expirable> implements Expirable {
        private Runnable onExpired;

        public OnExpiredRunnable(Expirable wrapped, Runnable onExpired) {
            super(wrapped);
            this.onExpired = onExpired;
        }

        @Override
        public boolean isExpired() {
            boolean expired = wrapped.isExpired();
            if(expired && onExpired != null) {
                Runnable callback = onExpired;
                onExpired = null;
                callback.run();
            }
            return expired;
        }
    }

}
