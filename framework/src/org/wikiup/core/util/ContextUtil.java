package org.wikiup.core.util;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupExpressionLanguage;
import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.impl.filter.TypeCastFilter;
import org.wikiup.core.impl.setter.BeanPropertySetter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ExpressionLanguage;
import org.wikiup.core.inf.Filter;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.inf.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ContextUtil {
    static public String getString(Getter<?> getter, String name, String defValue) {
        Object obj = getter.get(name);
        return obj != null ? obj.toString() : defValue;
    }

    static public <E> E get(Getter<E> getter, String name) {
        E obj = getter.get(name);
        Assert.notNull(obj, AttributeException.class, getter, name);
        return obj;
    }

    static public <E> E get(Getter<E> getter, String name, E defValue) {
        try {
            E obj = getter.get(name);
            return obj != null ? obj : defValue;
        } catch(Exception ex) {
            return defValue;
        }
    }

    static public void setProperty(Object obj, String path, Object value) {
        String[] paths = StringUtil.splitNamespaces(path);
        setProperty(obj, paths, value);
    }

    static public void setProperty(Object obj, String[] paths, Object value) {
        if(paths.length == 1)
            Interfaces.set(obj, paths[0], value);
        else {
            Getter<Object> getter = Interfaces.cast(Getter.class, obj);
            if(getter != null)
                Interfaces.set(getProperty(getter, paths, paths.length - 1), paths[paths.length - 1], value);
        }
    }

    static public Object getProperty(Getter<?> container, String path[]) {
        return getProperty(container, path, path.length, 0, null);
    }

    static public Object getProperty(Getter<?> container, String path[], int depth) {
        return getProperty(container, path, depth, 0, null);
    }

    static public Object getProperty(Getter<?> container, String path[], int depth, int offset) {
        return getProperty(container, path, depth, offset, null);
    }

    static public Object getProperty(Getter<?> container, String path[], int depth, int offset, Filter<Getter<?>, Getter<?>> filter) {
        int i;
        Object obj = container;
        for(i = offset; i < depth; i++)
            if(!StringUtil.isEmpty(path[i])) {
                obj = (filter != null ? filter.filter(container) : container).get(path[i]);
                if(obj instanceof Getter)
                    container = (Getter<?>) obj;
                else if(i != depth - 1)
                    return null;
            }
        return obj;
    }

    static public void setProperties(Document properties, Object obj, Getter<?> src) {
        ModelProvider mc = Interfaces.cast(ModelProvider.class, obj);
        Setter<?> dest = mc != null ? mc.getModel(Setter.class) : Interfaces.cast(Setter.class, obj);
        setProperties(properties, dest != null ? dest : new BeanPropertySetter(obj), src);
    }

    static public void setProperties(Document properties, Setter<?> dest, Getter<?> src) {
        if(properties != null)
            setProperties(properties.getChildren("property"), dest, src);
    }

    static public void setProperties(Iterable<Document> iterable, Setter<?> dest, Getter<?> src) {
        ExpressionLanguage<Getter<?>, Object> el = WikiupExpressionLanguage.getInstance();
        for(Document property : iterable) {
            Object value = el.evaluate(src, Documents.getDocumentValue(property));
            if(value != null)
                ((Setter<Object>) dest).set(Documents.getId(property), value);
        }
    }

    static public boolean parseNameValuePair(Setter<String> setter, String line, char equalChar) {
        return parseNameValuePair(setter, line, equalChar, false);
    }

    static public boolean parseNameValuePair(Setter<String> setter, String line, char equalChar, boolean quoted) {
        int pos = line.indexOf(equalChar);
        if(pos != -1) {
            String name = line.substring(0, pos);
            String value = line.substring(pos + 1);
            setter.set(name.trim(), quoted ? StringUtil.trim(value, "'\"") : value.trim());
        }
        return pos != -1;
    }

    static public Object getBeanProperty(Object instance, String name) {
        Method method = instance != null ? getBeanPropertyGetMethod(instance.getClass(), name) : null;
        try {
            return method != null ? method.invoke(instance) : null;
        } catch(IllegalAccessException ex) {
            Assert.fail(ex);
        } catch(InvocationTargetException ex) {
            Assert.fail(ex.getCause());
        }
        return null;
    }

    static public void setBeanProperty(Object instance, String name, Object value) {
        try {
            Method method = instance != null ? getBeanPropertySetMethod(instance.getClass(), name, value) : null;
            if(method != null) {
                Class<?> toClass = method.getParameterTypes()[0];
                if(value != null && !toClass.isAssignableFrom(value.getClass()))
                    value = Wikiup.getModel(TypeCastFilter.class).cast(toClass, value);
                method.invoke(instance, value);
            }
        } catch(Exception ex) {
            Assert.fail(ex);
        }
    }

    static public Method getBeanPropertyGetMethod(Class<?> clazz, String name) {
        Method method = Interfaces.getBeanMethod(clazz, "get" + getPropertyName(name));
        return method != null ? method : Interfaces.getBeanMethod(clazz, "is" + getPropertyName(name));
    }

    static public Method getBeanPropertySetMethod(Class<?> clazz, String name, Object obj) {
        return Interfaces.getBeanMethod(clazz, "set" + getPropertyName(name), obj);
    }

    static public String getPropertyName(String name) {
        return getPropertyName(name, true);
    }

    static public String getPropertyName(String name, boolean firstCapital) {
        if(firstCapital) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(Character.toUpperCase(name.charAt(0)));
            if(name.length() > 1)
                buffer.append(StringUtil.getCamelName(name.substring(1), '-'));
            return buffer.toString();
        }
        return StringUtil.getCamelName(name, '-');
    }
}
