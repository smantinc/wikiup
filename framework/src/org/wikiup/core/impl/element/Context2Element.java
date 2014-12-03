package org.wikiup.core.impl.element;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Element;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Setter;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.ValueUtil;

import java.util.Iterator;

public class Context2Element implements Element {
    private Getter<?> getter;
    private Setter<?> setter;
    private Iterable<String> iterable;
    private String name = "root";

    public Context2Element(Getter<?> getter, Setter<?> setter, Iterable<String> iterable) {
        this.getter = getter;
        this.setter = setter;
        this.iterable = iterable;
    }

    public Context2Element(Context<?, ?> context, Iterable<String> iterable) {
        this(context, context, iterable);
    }

    public Context2Element(Setter<?> setter, Iterable<String> iterable) {
        this(null, setter, iterable);
    }

    public Context2Element(Getter<?> getter, Iterable<String> iterable) {
        this(getter, null, iterable);
    }

    public Attribute getAttribute(String name) {
        return new ContextAttribute(name);
    }

    public Attribute addAttribute(String name) {
        return new ContextAttribute(name);
    }

    public void removeAttribute(Attribute attr) {
    }

    public Iterable<Attribute> getAttributes() {
        return new Iterable<Attribute>() {
            public Iterator<Attribute> iterator() {
                return iterable != null ? new ContextAttributeIterator(iterable.iterator()) : Null.getInstance();
            }
        };
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getObject() {
        return null;
    }

    public void setObject(Object obj) {
    }

    private class ContextAttributeIterator implements Iterator<Attribute> {
        private Iterator iterator;

        public ContextAttributeIterator(Iterator iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Attribute next() {
            return new ContextAttribute(ValueUtil.toString(iterator.next()));
        }

        public void remove() {
            iterator.remove();
        }
    }

    private class ContextAttribute implements Attribute {
        private String name;

        public ContextAttribute(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getObject() {
            return getter != null ? getter.get(name) : null;
        }

        public void setObject(Object obj) {
            if(setter != null)
                ((Setter) setter).set(name, obj);
        }

        @Override
        public String toString() {
            return ValueUtil.toString(getObject());
        }
    }
}
