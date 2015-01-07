package org.wikiup.core.impl.cl;

import org.wikiup.core.inf.ext.ClassDictionary;

public class ClassDictionaryImpl implements ClassDictionary {
    public Class get(String name) {
        try {
            return name != null ? Class.forName(name) : null;
        } catch(ClassNotFoundException e) {
            return null;
        }
    }
}
