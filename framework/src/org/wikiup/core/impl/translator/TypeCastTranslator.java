package org.wikiup.core.impl.translator;

import java.util.HashMap;
import java.util.Map;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Translator;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

public class TypeCastTranslator extends WikiupDynamicSingleton<TypeCastTranslator> implements Translator<Object, Object> {
    private Map<Class<?>, Map<Class<?>, Translator<Object, Object>>> typeTranslators;

    public Object translate(Object object) {
        return cast(Object.class, object);
    }

    public <F, T> T cast(Class<T> toClass, F from) {
        Map<Class<?>, Translator<Object, Object>> toFilter = typeTranslators.get(Interfaces.box(from.getClass()));
        if(toFilter != null) {
            toClass = (Class<T>) Interfaces.box(toClass);
            Translator<Object, Object> translator = toFilter.get(Interfaces.box(toClass));
            return translator != null ? Interfaces.cast(toClass, translator.translate(from)) : null;
        }
        return null;
    }

    @Override
    public void aware(Document desc) {
        for(Document node : desc.getChildren()) {
            Class<?> clazz = Interfaces.getClass(Documents.getDocumentValue(node, "from-class", null));
            typeTranslators.put(clazz, loadTranslators(node));
        }
    }

    private Map<Class<?>, Translator<Object, Object>> loadTranslators(Document node) {
        Map<Class<?>, Translator<Object, Object>> filters = new HashMap<Class<?>, Translator<Object, Object>>();
        for(Document item : node.getChildren()) {
            Class<?> clazz = Interfaces.getClass(Documents.getDocumentValue(item, "to-class", null));
            filters.put(clazz, Wikiup.getInstance().getBean(Translator.class, item));
        }
        return filters;
    }

    public void firstBuilt() {
        typeTranslators = new HashMap<Class<?>, Map<Class<?>, Translator<Object, Object>>>();
    }
}
