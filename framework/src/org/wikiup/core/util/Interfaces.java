package org.wikiup.core.util;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupClassLoader;
import org.wikiup.core.exception.WikiupRuntimeException;
import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.filter.TypeCastFilter;
import org.wikiup.core.impl.mp.GenericModelProvider;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ExceptionHandler;
import org.wikiup.core.inf.Translator;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.inf.Provider;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Interfaces {
    public static <E> E cast(Object obj) {
        return (E) obj;
    }

    public static <E> E cast(Class<E> clazz, Object object) {
        return object != null && clazz.isInstance(object) ? clazz.cast(object) : null;
    }

    public static <E> E castByProvider(Class<E> clazz, Object object) {
        E e = cast(clazz, object);
        if(e == null) {
            Provider<?> provider = cast(Provider.class, object);
            e = provider != null ? cast(clazz, provider.get()) : null;
        }
        return e;
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

    public static boolean initialize(ModelProvider mc, Document doc) {
        return mc != null ? initialize(mc.getModel(DocumentAware.class), doc) : false;
    }

    public static <E> Iterable<E> foreach(Object obj) {
        Iterable<E> iterable = Interfaces.cast(Iterable.class, obj);
        return iterable != null ? iterable : Null.getInstance();
    }

    public static Object get(Object obj) {
        Provider<?> provider = cast(Provider.class, obj);
        return provider != null ? provider.get() : obj;
    }

    public static <E> E get(Object object, String name) {
        Getter<E> getter = cast(Getter.class, object);
        return getter != null ? getter.get(name) : null;
    }

    public static <E> boolean set(Object object, String name, E value) {
        Setter<E> setter = cast(Setter.class, object);
        if(setter != null)
            setter.set(name, value);
        return setter != null;
    }

    public static void release(Object object) {
        Releasable releasable = cast(Releasable.class, object);
        if(releasable != null)
            releasable.release();
    }

    public static <E, R> R filter(Object obj, E from, R def) {
        if(obj instanceof Translator)
            return ((Translator<E, R>) obj).translate(from);
        return def;
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

    public static ModelProvider getModelContainer(Object obj) {
        return obj != null ? new GenericModelProvider(obj) : null;
    }

    public static <E> E getModel(Object obj, Class<E> clazz) {
        ModelProvider mc = getModelContainer(obj);
        return mc != null ? mc.getModel(clazz) : null;
    }

    public static <E> E getModelFromProvider(Object obj, Class<E> clazz) {
        Provider<?> p = Interfaces.cast(Provider.class, obj);
        ModelProvider mc = getModelContainer(p != null ? p.get() : obj);
        return mc != null ? mc.getModel(clazz) : null;
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

    public static <E> E newInstance(Class<E> clazz, String className) {
        Object instance = className != null ? newInstance(getClass(className)) : null;
        return cast(clazz, instance);
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
        TypeCastFilter typeCast = Wikiup.getModel(TypeCastFilter.class);
        Object[] params = new Object[args.length];
        int i;
        for(i = 0; i < args.length; i++)
            if(args[i] == null || paramTypes[i].isAssignableFrom(args[i].getClass()))
                params[i] = args[i];
            else if((params[i] = typeCast.cast(paramTypes[i], args[i])) == null)
                return null;
        return params;
    }
}
