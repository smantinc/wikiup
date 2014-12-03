package org.wikiup.core.impl.iterable;

import org.wikiup.core.impl.attribute.MapAttribute;
import org.wikiup.core.inf.Attribute;

import java.util.Iterator;
import java.util.Map;

public class MapAttributes implements Iterable<Attribute> {
    private Map<String, Object> map;

    public MapAttributes(Map<String, Object> map) {
        this.map = map;
    }

    public Iterator<Attribute> iterator() {
        return new MapAttributeIterator(map);
    }

    private static class MapAttributeIterator implements Iterator<Attribute> {
        private Iterator<String> iterator;
        private Map<String, Object> map;

        public MapAttributeIterator(Map<String, Object> map) {
            this.map = map;
            iterator = map.keySet().iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Attribute next() {
            return new MapAttribute(map, iterator.next());
        }

        public void remove() {
            iterator.remove();
        }
    }
}
