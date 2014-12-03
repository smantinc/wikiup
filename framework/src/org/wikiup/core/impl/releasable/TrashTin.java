package org.wikiup.core.impl.releasable;

import org.wikiup.core.inf.Releasable;
import org.wikiup.core.util.Interfaces;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class TrashTin implements Releasable {
    private List<Releasable> container;
    private int capacity;

    public TrashTin(int capacity) {
        this.capacity = capacity;
        container = new Vector<Releasable>();
    }

    public TrashTin() {
        this(200);
    }

    public TrashTin(List<Releasable> container) {
        this.container = container;
    }

    public void release() {
        synchronized(container) {
            Interfaces.releaseAll(container);
            container.clear();
        }
    }

    public Iterator<Releasable> elements() {
        return container.iterator();
    }

    public void put(Releasable element) {
        synchronized(container) {
            if(container.size() >= capacity)
                container.remove(0).release();
            if(!container.contains(element))
                container.add(element);
        }
    }

    public void remove(Releasable element) {
        container.remove(element);
    }
}
