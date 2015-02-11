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
import org.wikiup.core.impl.translator.TypeCastFilter;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Decorator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ExceptionHandler;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.Setter;
import org.wikiup.core.inf.Translator;
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
        return Wrappers.unwrap(obj);
    }

    public static <E> E get(Object object, String name) {
        Getter<E> getter = cast(Getter.class, object);
        return getter != null ? getter.get(name) : null;
    }

    public static <T> T wire(Class<T> clazz, Object obj, Document doc) {
        Wirable.ByDocument<T> wirable = cast(Wirable.ByDocument.class, obj);
        return wirable != null ? wirable.wire(doc) : Interfaces.cast(clazz, obj);
    }

    public static <T> T decorate(T obj, Object decorator, Document doc) {
        Decorator<T> decoratorImpl = cast(Decorator.class, decorator);
        return decoratorImpl != null ? decoratorImpl.decorate(obj, doc) : obj;
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

    public static Class<?> box(Class<?> clazz) {
        return clazz.isPrimitive() ? PRIMITIVES.get(clazz.getName()) : clazz;
    }
}
