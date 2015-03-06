package org.wikiup.core.impl.iterable;

import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.inf.Dictionary;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BeanProperties implements Iterable<BeanProperty>, Dictionary<BeanProperty> {
    private Map<String, BeanProperty> properties = new HashMap<String, BeanProperty>();

    public BeanProperties(Class clazz, boolean onlyDeclaredMethod) {
        loadProperties(null, clazz, onlyDeclaredMethod);
    }

    public BeanProperties(Object instance, boolean onlyDeclaredMethod) {
        loadProperties(instance, instance.getClass(), onlyDeclaredMethod);
    }

    public BeanProperties(Object instance) {
        this(instance, false);
    }

    public BeanProperty get(String name) {
        return properties.get(name);
    }

    public Iterator<BeanProperty> iterator() {
        return properties.values().iterator();
    }

    private void loadProperties(Object instance, Class<?> clazz, boolean onlyDeclaredMethod) {
        Method[] methods = onlyDeclaredMethod ? clazz.getDeclaredMethods() : clazz.getMethods();
        for(Method method : methods) {
            String methodName = method.getName();
            int modifiers = method.getModifiers();
            if(methodName.length() > 3 && Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
                String propertyName = getPropertyName(methodName);
                if(!properties.containsKey(propertyName)) {
                    if(methodName.startsWith("get") && method.getParameterTypes().length == 0)
                        properties.put(propertyName, new BeanProperty(instance, propertyName, method.getReturnType()));
                    else if(methodName.startsWith("set") && method.getParameterTypes().length == 1)
                        properties.put(propertyName, new BeanProperty(instance, propertyName, method.getParameterTypes()[0]));
                }
            }
        }
    }

    private String getPropertyName(String methodName) {
        return Character.toLowerCase(methodName.charAt(3)) + (methodName.length() > 4 ? methodName.substring(4) : "");
    }
}
