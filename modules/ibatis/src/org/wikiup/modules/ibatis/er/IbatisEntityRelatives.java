package org.wikiup.modules.ibatis.er;

import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.attribute.MapAttribute;
import org.wikiup.core.impl.document.Context2Document;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.impl.iterable.BeanPropertyNames;
import org.wikiup.core.impl.iterable.MapAttributes;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.database.orm.EntityRelatives;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.modules.ibatis.entity.AbstractEntity;
import org.wikiup.modules.ibatis.entity.MapEntity;
import org.wikiup.modules.ibatis.entity.PojoEntity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IbatisEntityRelatives extends EntityRelatives {
    private List<Object> relatives;
    private String name;

    public IbatisEntityRelatives(String name, List<Object> relatives) {
        this.relatives = relatives;
        this.name = name;
    }

    public Document addChild(String name) {
        return null;
    }

    public Attribute getAttribute(String name) {
        Object obj = relatives.isEmpty() ? null : relatives.get(0);
        return obj instanceof Map ? new MapAttribute((Map) obj, name) : obj != null ? new BeanProperty(obj, name) : null;
    }

    public Iterable<Attribute> getAttributes() {
        Object obj = relatives.isEmpty() ? null : relatives.get(0);
        return obj instanceof Map ? new MapAttributes((Map) obj) : obj != null ? new BeanProperties(obj) : Null.getInstance();
    }

    public Document getChild(String name) {
        return null;
    }

    public Iterable<Document> getChildren() {
        return new Iterable<Document>() {
            public Iterator<Document> iterator() {
                return new IbatisEntityRelativesIterator();
            }
        };
    }

    public Iterable<Document> getChildren(String name) {
        return null;
    }

    public Document getParentNode() {
        return null;
    }

    public void removeNode(Document child) {
    }

    private class IbatisEntityRelativesIterator implements Iterator<Document> {
        private Iterator iterator;

        public IbatisEntityRelativesIterator() {
            iterator = relatives.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Document next() {
            Object object = iterator.next();
            AbstractEntity entity = object instanceof Map ? new MapEntity(name, (Map<String, Object>) object) : new PojoEntity(name, object);
            Iterable<String> iterable = object instanceof Map ? ((Map<String, Object>) object).keySet() : new BeanPropertyNames(object.getClass());
            return new IbatisEntityRelative(entity, iterable);
        }

        public void remove() {
            iterator.remove();
        }
    }

    private static class IbatisEntityRelative extends Context2Document {
        public IbatisEntityRelative(EntityModel entityModel, Iterable<String> iterable) {
            super(entityModel, iterable);
            setName("relative");
        }
    }
}
