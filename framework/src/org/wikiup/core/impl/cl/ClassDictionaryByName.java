package org.wikiup.core.impl.cl;

import org.wikiup.core.Constants;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.inf.ext.ClassDictionary;
import org.wikiup.core.util.Documents;

import java.util.HashMap;
import java.util.Map;

public class ClassDictionaryByName implements ClassDictionary {
    final private Map<String, Class> dictionary = new HashMap<String, Class>();

    public ClassDictionaryByName() {
    }

    public ClassDictionaryByName(Document desc) {
        this(desc, new ClassDictionaryImpl());
    }

    public ClassDictionaryByName(Document desc, ClassDictionary classDictionary) {
        wire(desc, classDictionary);
    }

    public void wire(Document desc, ClassDictionary classDictionary) {
        for(Document node : desc.getChildren()) {
            String name = Documents.getId(node);
            Class clazz = classDictionary.get(Documents.ensureAttributeValue(node, Constants.Attributes.CLASS));
            dictionary.put(name, clazz);
        }
    }

    @Override
    public Class get(String name) {
        return dictionary.get(name);
    }
}
