package org.wikiup.core.util;

import org.wikiup.core.exception.WikiupRuntimeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Assert {
    static public void fail(Throwable ex) {
        if(ex instanceof InvocationTargetException)
            ex = ex.getCause();
        if(ex instanceof RuntimeException)
            throw (RuntimeException) ex;
        if(ex instanceof Error)
            throw (Error) ex;
        throw new WikiupRuntimeException(ex);
    }

    static public <E extends Exception> void fail(Class<E> clazz) {
        try {
            throw getRuntimeException(clazz.newInstance());
        } catch(InstantiationException ex) {
        } catch(IllegalAccessException ex) {
        }
    }

    static public <E extends Exception> void fail(Class<E> clazz, Object... args) {
        Constructor<E> constructor = null;
        Object arg = args;
        try {
            constructor = clazz.getConstructor(Object[].class);
        } catch(NoSuchMethodException e) {
            try {
                if(args.length == 1 && args[0] instanceof String) {
                    constructor = clazz.getConstructor(String.class);
                    arg = args[0];
                }
            } catch(NoSuchMethodException e1) {
            }
        }

        try {
            if(constructor != null)
                throw getRuntimeException(constructor.newInstance(arg));
        } catch(InvocationTargetException ex) {
        } catch(IllegalAccessException ex) {
        } catch(InstantiationException ex) {
        }
    }

    static public void isTrue(boolean s, String message) {
        if(!s)
            fail(WikiupRuntimeException.class, message);
    }

    static public <E extends Exception> void isTrue(boolean s, Class<E> clazz, Object... args) {
        if(!s)
            fail(clazz, args);
    }

    static public <E extends Exception> void isTrue(boolean s, Class<E> clazz) {
        if(!s)
            fail(clazz);
    }

    static public void isTrue(boolean s) {
        if(!s)
            fail(new WikiupRuntimeException());
    }

    static public <R> R notNull(R s, String message) {
        if(s == null)
            fail(WikiupRuntimeException.class, message);
        return s;
    }

    static public <E extends Exception, R> R notNull(R s, Class<E> clazz, Object... args) {
        if(s == null)
            fail(clazz, args);
        return s;
    }

    static public <E extends Exception, R> R notNull(R s, Class<E> clazz) {
        if(s == null)
            fail(clazz);
        return s;
    }

    static public <E> E notNull(E s) {
        if(s == null)
            fail(new WikiupRuntimeException());
        return s;
    }

    static private RuntimeException getRuntimeException(Exception ex) {
        return ex instanceof RuntimeException ? (RuntimeException) ex : new WikiupRuntimeException(ex);
    }
}
