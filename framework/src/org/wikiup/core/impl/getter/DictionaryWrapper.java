package org.wikiup.core.impl.getter;

import org.wikiup.core.inf.Dictionary;

public class DictionaryWrapper<E> implements Dictionary<E> {
    private Dictionary<E> dictionary;

    public DictionaryWrapper() {
    }

    public DictionaryWrapper(Dictionary<E> dictionary) {
        setGetter(dictionary);
    }

    public E get(String name) {
        return dictionary.get(name);
    }

    public void setGetter(Dictionary<E> dictionary) {
        this.dictionary = dictionary;
    }
}
