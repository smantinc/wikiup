package org.wikiup.servlet.impl.bindable;

import org.wikiup.core.impl.dictionary.DictionaryWrapper;
import org.wikiup.core.impl.setter.BeanPropertySetter;
import org.wikiup.core.inf.Bindable;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.ContextUtil;

public class ByPropertyManually extends DictionaryWrapper<Object> implements Bindable, DocumentAware {
    private Document properties;

    public ByPropertyManually(Dictionary<Object> dictionary) {
        super(dictionary);
    }

    public void bind(Object object) {
        if(properties != null)
            ContextUtil.setProperties(properties, new BeanPropertySetter(object), this);
    }

    public void aware(Document desc) {
        properties = desc;
    }
}