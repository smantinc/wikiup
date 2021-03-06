package org.wikiup.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupClassLoader;
import org.wikiup.core.exception.WikiupRuntimeException;
import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.mp.GenericModelProvider;
import org.wikiup.core.bean.WikiupTypeTranslator;
import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ExceptionHandler;
import org.wikiup.core.inf.Expirable;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.Wrapper;
import org.wikiup.core.inf.ext.Wirable;

public class Interfaces {
    static private Map<String, Class<?>> PRIMITIVES = new HashMap<String, Class<?>>();

    static {
        PRIMITIVES.put("int", Integer.class);
        PRIMITIVES.put("long", Long.class);
        PRIMITIVES.put("short", Short.class);
        PRIMITIVES.put("boolean", Boolean.class);
        PRIMITIVES.put("float", Float.class);
        PRIMITIVES.put("double", Double.class);
        PRIMITIVES.put("byte", Byte.class);
    }

    public static <E> E cast(Object obj) {
        return (E) obj;
    }

    public static <E> E cast(Class<E> clazz, Object object) {
        return object != null && clazz.isInstance(object) ? clazz.cast(object) : null;
    }

    public static <E> E findAssignable(Class<E> clazz, Object... objects) {
        for(Object obj : objects) {
            E inst = cast(clazz, obj);
            if(inst != null)
                return inst;
        }
        return null;
    }

    public static boolean initialize(Object obj, Document doc) {
        if(doc != null) {
            DocumentAware aware = cast(DocumentAware.class, obj);
            if(aware != null)
                aware.aware(doc);
            return aware != null;
        }
        return false;
    }

    public static boolean initialize(BeanContainer mc, Document doc) {
        return mc != null ? initialize(mc.query(DocumentAware.class), doc) : false;
    }

    public static <E> Iterable<E> foreach(Object obj) {
        Iterable<E> iterable = Interfaces.cast(Iterable.class, obj);
        return iterable != null ? iterable : Null.getInstance();
    }

    public static Object get(Object obj) {
        return unwrap(obj);
    }

    public static <E> E get(Object object, String name) {
        Dictionary<E> dictionary = cast(Dictionary.class, object);
        return dictionary != null ? dictionary.get(name) : null;
    }

    public static <T, P> T wire(Class<T> clazz, Object obj, P wire) {
        Wirable<T, P> wirable = cast(Wirable.class, obj);
        return wirable != null ? wirable.wire(wire) : Interfaces.cast(clazz, obj);
    }

    public static <E> boolean set(Object object, String name, E value) {
        Dictionary.Mutable<E> mutable = cast(Dictionary.Mutable.class, object);
        if(mutable != null)
            mutable.set(name, value);
        return mutable != null;
    }

    public static void release(Object object) {
        Releasable releasable = cast(Releasable.class, object);
        if(releasable != null)
            releasable.release();
    }

    public static void releaseAll(Iterable<?> iterable) {
        for(Object obj : iterable)
            release(obj);
    }

    public static Exception stripException(Exception exp) {
        WikiupRuntimeException runtime = Interfaces.cast(WikiupRuntimeException.class, exp);
        return runtime != null && runtime.getCause() != null ? Interfaces.cast(Exception.class, runtime.getCause()) : exp;
    }

    public static boolean handleException(Object handler, Exception ex) {
        ExceptionHandler eh = cast(ExceptionHandler.class, handler);
        return eh != null ? eh.handle(ex) : false;
    }

    @Deprecated
    public static BeanContainer getModelContainer(Object obj) {
        return obj != null ? new GenericModelProvider(obj) : null;
    }

    public static <E> E getModel(Object obj, Class<E> clazz) {
        BeanContainer mc = getModelContainer(obj);
        return mc != null ? mc.query(clazz) : null;
    }

    public static <E> E getModel(Iterable<?> iterable, Class<E> clazz) {
        for(Object obj : iterable) {
            E model = getModel(obj, clazz);
            if(model != null)
                return model;
        }
        return null;
    }

    public static Class tryGetClass(String className) {
        return getClass(className, false);
    }

    public static Class getClass(String className) {
        return getClass(className, true);
    }

    public static Class getClass(String className, boolean alert) {
        try {
            WikiupClassLoader cl = Wikiup.getModel(WikiupClassLoader.class);
            return cl.get(className);
        } catch(Exception ex) {
            if(alert)
                Assert.fail(ex);
        }
        return null;
    }

    public static <E> E newInstance(Class<E> clazz) {
        try {
            return clazz.newInstance();
        } catch(InstantiationException e) {
            Assert.fail(e);
        } catch(IllegalAccessException e) {
            Assert.fail(e);
        }
        return null;
    }

    public static <T> T newInstance(Class<T> clazz, Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        LinkedList<Constructor<T>> constructors = new LinkedList<Constructor<T>>((List) Arrays.asList(clazz.getConstructors()));
        while(!constructors.isEmpty()) {
            int i;
            Constructor<T> constructor = constructors.pop();
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            for(i = 0; i < parameterTypes.length - 1; i++) {
                Object arg = args[i];
                if(arg != null && !box(parameterTypes[i]).isAssignableFrom(args[i].getClass())) {
                    constructor = null;
                    break;
                }
            }
            if(constructor != null) {
                if(args.length - 1 == i) {
                    if(args[i] == null || parameterTypes[i].isAssignableFrom(args[i].getClass()))
                        return constructor.newInstance(args);
                } else if(parameterTypes.length > 0) {
                    if(parameterTypes[parameterTypes.length - 1].equals(Object[].class)) {
                        Object[] params = Arrays.copyOfRange(args, 0, parameterTypes.length);
                        Object[] extras = Arrays.copyOfRange(args, parameterTypes.length - 1, args.length);
                        params[parameterTypes.length - 1] = extras;
                        return constructor.newInstance(params);
                    }
                }
            }
        }
        return null;
    }

    public static Object invoke(Object instance, String methodName, Object... args) throws NoSuchMethodException {
        Class<?> clazz = instance.getClass();
        for(Method method : clazz.getMethods()) {
            if(Modifier.isPublic(method.getModifiers()) && method.getName().equals(methodName)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if(args.length == paramTypes.length) {
                    Object[] params = typeCast(paramTypes, args);
                    if(params != null)
                        try {
                            return method.invoke(instance, params);
                        } catch(Exception e) {
                            Assert.fail(e);
                        }
                }
            }
        }
        throw new NoSuchMethodException(methodName);
    }

    public static Method getBeanMethod(Class<?> clazz, String methodName, Object... args) {
        for(Method method : clazz.getMethods()) {
            if(Modifier.isPublic(method.getModifiers()) && method.getName().equals(methodName)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if(args.length == paramTypes.length) {
                    Object[] params = typeCast(paramTypes, args);
                    if(params != null)
                        return method;
                }
            }
        }
        return null;
    }

    public static Object[] typeCast(Class<?>[] paramTypes, Object[] args) {
        WikiupTypeTranslator typeCast = Wikiup.getModel(WikiupTypeTranslator.class);
        Object[] params = new Object[args.length];
        int i;
        for(i = 0; i < args.length; i++)
            if(args[i] == null || paramTypes[i].isAssignableFrom(args[i].getClass()))
                params[i] = args[i];
            else if((params[i] = typeCast.cast(paramTypes[i], args[i])) == null)
                return null;
        return params;
    }

    public static Class<?> box(Class<?> clazz) {
        return clazz.isPrimitive() ? PRIMITIVES.get(clazz.getName()) : clazz;
    }

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
