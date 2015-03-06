package org.wikiup.core.impl.dictionary;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Translator;

public class DictionaryByDocument extends WrapperImpl<Document> implements Dictionary<Object> {
    public DictionaryByDocument(Document wrapped) {
        super(wrapped);
    }

    @Override
    public Object get(String name) {
        Attribute attribute = wrapped.getAttribute(name);
        return attribute != null ? attribute.getObject() : null;
    }
    
    public static final class TRANSLATOR implements Translator<Document, DictionaryByDocument> {
        @Override
        public DictionaryByDocument translate(Document doc) {
            return new DictionaryByDocument(doc);
        }
    }
}
