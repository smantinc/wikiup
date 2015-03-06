package org.wikiup.core.util;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupExpressionLanguage;
import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.impl.setter.BeanPropertySetter;
import org.wikiup.core.impl.translator.TypeCastTranslator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ExpressionLanguage;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Translator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ContextUtil {
    static public String getString(Dictionary<?> dictionary, String name, String defValue) {
        Object obj = dictionary.get(name);
        return obj != null ? obj.toString() : defValue;
    }

    static public <E> E get(Dictionary<E> dictionary, String name) {
        E obj = dictionary.get(name);
        Assert.notNull(obj, AttributeException.class, dictionary, name);
        return obj;
    }

    static public <E> E get(Dictionary<E> dictionary, String name, E defValue) {
        try {
            E obj = dictionary.get(name);
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
            Dictionary<Object> dictionary = Interfaces.cast(Dictionary.class, obj);
            if(dictionary != null)
                Interfaces.set(getProperty(dictionary, paths, paths.length - 1), paths[paths.length - 1], value);
        }
    }

    static public Object getProperty(Dictionary<?> container, String path[]) {
        return getProperty(container, path, path.length, 0, null);
    }

    static public Object getProperty(Dictionary<?> container, String path[], int depth) {
        return getProperty(container, path, depth, 0, null);
    }

    static public Object getProperty(Dictionary<?> container, String path[], int depth, int offset) {
        return getProperty(container, path, depth, offset, null);
    }

    static public Object getProperty(Dictionary<?> container, String path[], int depth, int offset, Translator<Dictionary<?>, Dictionary<?>> translator) {
        int i;
        Object obj = container;
        for(i = offset; i < depth; i++)
            if(!StringUtil.isEmpty(path[i])) {
                obj = (translator != null ? translator.translate(container) : container).get(path[i]);
                if(obj instanceof Dictionary)
                    container = (Dictionary<?>) obj;
                else if(i != depth - 1)
                    return null;
            }
        return obj;
    }

    static public void setProperties(Document properties, Object obj, Dictionary<?> src) {
        BeanContainer mc = Interfaces.cast(BeanContainer.class, obj);
        Dictionary.Mutable<?> dest = mc != null ? mc.query(Dictionary.Mutable.class) : Interfaces.cast(Dictionary.Mutable.class, obj);
        setProperties(properties, dest != null ? dest : new BeanPropertySetter(obj), src);
    }

    static public void setProperties(Document properties, Dictionary.Mutable<?> dest, Dictionary<?> src) {
        if(properties != null)
            setProperties(properties.getChildren("property"), dest, src);
    }

    static public void setProperties(Iterable<Document> iterable, Dictionary.Mutable<?> dest, Dictionary<?> src) {
        ExpressionLanguage<Dictionary<?>, Object> el = WikiupExpressionLanguage.getInstance();
        for(Document property : iterable) {
            Object value = el.evaluate(src, Documents.getDocumentValue(property));
            if(value != null)
                ((Dictionary.Mutable<Object>) dest).set(Documents.getId(property), value);
        }
    }

    static public boolean parseNameValuePair(Dictionary.Mutable<String> mutable, String line, char equalChar) {
        return parseNameValuePair(mutable, line, equalChar, false);
    }

    static public boolean parseNameValuePair(Dictionary.Mutable<String> mutable, String line, char equalChar, boolean quoted) {
        int pos = line.indexOf(equalChar);
        if(pos != -1) {
            String name = line.substring(0, pos);
            String value = line.substring(pos + 1);
            mutable.set(name.trim(), quoted ? StringUtil.trim(value, "'\"") : value.trim());
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
                    value = Wikiup.getModel(TypeCastTranslator.class).cast(toClass, value);
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
            StringBuilder buffer = new StringBuilder();
            buffer.append(Character.toUpperCase(name.charAt(0)));
            if(name.length() > 1)
                buffer.append(StringUtil.getCamelName(name.substring(1), '-'));
            return buffer.toString();
        }
        return StringUtil.getCamelName(name, '-');
    }
}
