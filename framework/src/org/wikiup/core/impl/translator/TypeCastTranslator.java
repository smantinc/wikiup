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
        Map<Class<?>, Translator<Object, Object>> toFilter = typeTranslators.get(Interfaces.box(toClass));
        if(toFilter != null) {
            Class<?> fromClass = Interfaces.box(from.getClass());
            Translator<Object, Object> translator = toFilter.get(Interfaces.box(fromClass));
            return translator != null ? Interfaces.cast(toClass, translator.translate(from)) : null;
        }
        return null;
    }

    @Override
    public void aware(Document desc) {
        for(Document node : desc.getChildren()) {
            Class<?> fromClass = Interfaces.getClass(Documents.getDocumentValue(node, "from-class", null));
            loadTranslators(fromClass, node);
        }
    }

    private void loadTranslators(Class<?> fromClass, Document node) {
        for(Document item : node.getChildren()) {
            Class<?> toClass = Interfaces.getClass(Documents.getDocumentValue(item, "to-class", null));
            Translator<Object, Object> translator = Wikiup.getInstance().getBean(Translator.class, item);
            addTranslator(fromClass, toClass, translator);
        }
    }
    
    private void addTranslator(Class<?> fromClass, Class<?> toClass, Translator<Object, Object> translator) {
        Map<Class<?>, Translator<Object, Object>> translators = typeTranslators.get(toClass);
        if(translators == null)
            typeTranslators.put(toClass, translators = new HashMap<Class<?>, Translator<Object, Object>>());
        translators.put(fromClass, translator);
    }

    public void firstBuilt() {
        typeTranslators = new HashMap<Class<?>, Map<Class<?>, Translator<Object, Object>>>();
    }
}
