package org.wikiup.servlet.impl.context.util;

import org.wikiup.core.impl.dictionary.NamespaceDictionary;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;

import java.util.Iterator;
import java.util.Random;

public class RandomContentDictionary implements Dictionary<Object>, Iterable<String> {
    private NamespaceDictionary getter = new NamespaceDictionary();

    public RandomContentDictionary() {
        getter.addNamespace("integer", new RandomIntegerDictionary());
        getter.addNamespace("string", new RandomTextDictionary());
    }

    public Object get(String name) {
        return getter.get(name);
    }

    public Iterator<String> iterator() {
        return getter.iterator();
    }

    static class RandomIntegerDictionary implements Dictionary<String> {
        public String get(String name) {
            Random rnd = new Random();
            int h = ValueUtil.toInteger(name, -1);
            return h != -1 ? String.valueOf(rnd.nextInt(h)) : toString();
        }

        @Override
        public String toString() {
            Random rnd = new Random();
            return String.valueOf(rnd.nextInt());
        }
    }

    static class RandomTextDictionary implements Dictionary<String> {
        public String get(String name) {
            return StringUtil.generateRandomString(ValueUtil.toInteger(name, 8));
        }

        @Override
        public String toString() {
            return StringUtil.generateRandomString(8);
        }
    }
}
