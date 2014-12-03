package org.wikiup.servlet.impl.context.util;

import org.wikiup.core.impl.getter.NamespaceGetter;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;

import java.util.Iterator;
import java.util.Random;

public class RandomContentGetter implements Getter<Object>, Iterable<String> {
    private NamespaceGetter getter = new NamespaceGetter();

    public RandomContentGetter() {
        getter.addNamespace("integer", new RandomIntegerGetter());
        getter.addNamespace("string", new RandomTextGetter());
    }

    public Object get(String name) {
        return getter.get(name);
    }

    public Iterator<String> iterator() {
        return getter.iterator();
    }

    static class RandomIntegerGetter implements Getter<String> {
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

    static class RandomTextGetter implements Getter<String> {
        public String get(String name) {
            return StringUtil.generateRandomString(ValueUtil.toInteger(name, 8));
        }

        @Override
        public String toString() {
            return StringUtil.generateRandomString(8);
        }
    }
}
