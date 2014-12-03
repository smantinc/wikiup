package org.wikiup.core.impl.element;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomContainer<E> implements Getter<E> {
    private Map<String, List<E>> groups = new HashMap<String, List<E>>();
    private List<E> objects = new ArrayList<E>();

    public E get(String name) {
        List<E> group = getElementsByName(name, false);
        return group != null ? group.get(0) : null;
    }

    public E add(String name, E obj) {
        objects.add(obj);
        getElementsByName(name, true).add(obj);
        return obj;
    }

    public void remove(String name, E obj) {
        remove(name, getElementsByName(name, false), obj);
    }

    public Iterable<E> iterable() {
        return objects;
    }

    public Iterable<E> iterable(String name) {
        List<E> list = getElementsByName(name, false);
        return list != null ? list : Null.getInstance();
    }

    public int size() {
        return objects.size();
    }

    private List<E> getElementsByName(String name, boolean autoCreate) {
        List<E> r = groups.get(name);
        if(autoCreate && r == null)
            groups.put(name, (r = new ArrayList<E>()));
        return r;
    }

    private void remove(String childName, List<E> group, E obj) {
        if(group != null) {
            objects.remove(obj);
            group.remove(obj);
            if(group.isEmpty())
                groups.remove(childName);
        }
    }
}
