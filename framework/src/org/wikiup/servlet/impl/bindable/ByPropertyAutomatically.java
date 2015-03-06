package org.wikiup.servlet.impl.bindable;

import org.wikiup.core.inf.Bindable;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;

import java.util.HashSet;
import java.util.Set;

public class ByPropertyAutomatically implements Bindable, DocumentAware {
    private Dictionary<?> dictionary;
    private BeanContainer modelProvider;
    private Document configure;

    public ByPropertyAutomatically(Dictionary<Object> dictionary, BeanContainer modelProvider) {
        this.dictionary = dictionary;
        this.modelProvider = modelProvider;
    }

    public void bind(Object object) {
        Once once = new Once();
        ByPropertyManually manually = new ByPropertyManually(once);
        ByPropertyNames byNames = new ByPropertyNames(once);
        ByPropertyTypes byTypes = new ByPropertyTypes(once);
        manually.aware(configure);
        manually.bind(object);
        byNames.bind(object);
        byTypes.bind(object);
    }

    public void aware(Document desc) {
        configure = desc;
    }

    private class Once implements Dictionary<Object>, BeanContainer {
        private Set<String> setNames = new HashSet<String>();
        private Set<Class<?>> setClasses = new HashSet<Class<?>>();

        public Object get(String name) {
            boolean in = setNames.contains(name);
            setNames.add(name);
            Object obj = in ? null : dictionary.get(name);
            if(obj != null)
                setClasses.add(obj.getClass());
            return obj;
        }

        public <E> E query(Class<E> clazz) {
            return setClasses.contains(clazz) ? null : modelProvider.query(clazz);
        }
    }
}