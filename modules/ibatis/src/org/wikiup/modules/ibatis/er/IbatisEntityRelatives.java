package org.wikiup.modules.ibatis.er;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.attribute.MapAttribute;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.impl.iterable.MapAttributes;
import org.wikiup.core.inf.Attribute;
import org.wikiup.database.orm.imp.relatives.RelativesByEntity;
import org.wikiup.database.orm.inf.Relatives;
import org.wikiup.modules.ibatis.entity.AbstractEntity;
import org.wikiup.modules.ibatis.entity.MapEntity;
import org.wikiup.modules.ibatis.entity.PojoEntity;

public class IbatisEntityRelatives implements Relatives.OneToMany {
    private List<Object> relatives;
    private String name;

    public IbatisEntityRelatives(String name, List<Object> relatives) {
        this.relatives = relatives;
        this.name = name;
    }

    public Attribute get(String name) {
        Object obj = relatives.isEmpty() ? null : relatives.get(0);
        return obj instanceof Map ? new MapAttribute((Map) obj, name) : obj != null ? new BeanProperty(obj, name) : null;
    }

    public Iterable<Attribute> getProperties() {
        Object obj = relatives.isEmpty() ? null : relatives.get(0);
        return obj instanceof Map ? new MapAttributes((Map) obj) : obj != null ? new BeanProperties(obj) : Null.getInstance();
    }

    public Iterator<Relatives> iterator() {
        return new IbatisEntityRelativesIterator();
    }

    private class IbatisEntityRelativesIterator implements Iterator<Relatives> {
        private Iterator iterator;

        public IbatisEntityRelativesIterator() {
            iterator = relatives.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Relatives next() {
            Object object = iterator.next();
            AbstractEntity entity = object instanceof Map ? new MapEntity(name, (Map<String, Object>) object) : new PojoEntity(name, object);
            return new RelativesByEntity(entity);
        }

        public void remove() {
            iterator.remove();
        }
    }
}
