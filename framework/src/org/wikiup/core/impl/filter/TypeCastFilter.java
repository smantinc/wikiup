package org.wikiup.core.impl.filter;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Filter;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.Map;

public class TypeCastFilter extends WikiupDynamicSingleton<TypeCastFilter> implements Filter<Object, Object> {
    private Map<Class<?>, Map<Class<?>, Filter<Object, Object>>> typeFilters;

    static private Map<String, Class<?>> primitives = new HashMap<String, Class<?>>();

    static {
        primitives.put("int", Integer.class);
        primitives.put("long", Long.class);
        primitives.put("short", Short.class);
        primitives.put("boolean", Boolean.class);
        primitives.put("float", Float.class);
        primitives.put("double", Double.class);
        primitives.put("byte", Byte.class);
    }

    public Object filter(Object object) {
        return cast(Object.class, object);
    }

    public <F, T> T cast(Class<T> toClass, F from) {
        Map<Class<?>, Filter<Object, Object>> toFilter = typeFilters.get(box(from.getClass()));
        if(toFilter != null) {
            toClass = (Class<T>) box(toClass);
            Filter<Object, Object> filter = toFilter.get(box(toClass));
            return filter != null ? Interfaces.cast(toClass, filter.filter(from)) : null;
        }
        return null;
    }

    @Override
    public void aware(Document desc) {
        for(Document node : desc.getChildren()) {
            Class<?> clazz = Interfaces.getClass(Documents.getDocumentValue(node, "from-class", null));
            typeFilters.put(clazz, loadFilters(node));
        }
    }

    private Class<?> box(Class<?> clazz) {
        return clazz.isPrimitive() ? primitives.get(clazz.getName()) : clazz;
    }

    private Map<Class<?>, Filter<Object, Object>> loadFilters(Document node) {
        Map<Class<?>, Filter<Object, Object>> filters = new HashMap<Class<?>, Filter<Object, Object>>();
        for(Document item : node.getChildren()) {
            Class<?> clazz = Interfaces.getClass(Documents.getDocumentValue(item, "to-class", null));
            filters.put(clazz, Wikiup.getInstance().getBean(Filter.class, item));
        }
        return filters;
    }

//  public void cloneFrom(TypeCastFilter instance)
//  {
//    typeFilters = instance.typeFilters;
//  }

    public void firstBuilt() {
        typeFilters = new HashMap<Class<?>, Map<Class<?>, Filter<Object, Object>>>();
    }
}
