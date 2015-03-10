package org.wikiup.core.impl.dictionary;

import org.wikiup.core.impl.wrapper.WrapperImpl;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Translator;
import org.wikiup.core.util.Documents;

public class DictionaryByDocument extends WrapperImpl<Document> implements Dictionary<Object> {
    public DictionaryByDocument(Document wrapped) {
        super(wrapped);
    }

    @Override
    public Object get(String name) {
        return Documents.getDocumentValue(wrapped, name);
    }
    
    public static final class TRANSLATOR implements Translator<Document, DictionaryByDocument> {
        @Override
        public DictionaryByDocument translate(Document doc) {
            return new DictionaryByDocument(doc);
        }
    }
}
