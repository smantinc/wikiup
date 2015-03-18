package org.wikiup.modules.hibernate.er;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Session;
import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.attribute.AttributeImpl;
import org.wikiup.core.inf.Attribute;
import org.wikiup.database.orm.Entity;
import org.wikiup.database.orm.imp.relatives.RelativesByEntity;
import org.wikiup.database.orm.inf.Relatives;
import org.wikiup.modules.hibernate.entity.DynamicMapEntity;
import org.wikiup.modules.hibernate.entity.PojoEntity;

public class CollectionEntityRelatives implements Relatives.OneToMany {
    private Collection<Object> relatives;
    private String name;
    private Session session;

    public CollectionEntityRelatives(Session session, String name, Collection<Object> relatives) {
        this.session = session;
        this.name = name;
        this.relatives = relatives;
    }

    public Attribute get(String name) {
        return "size".equals(name) ? new AttributeImpl(name, relatives.size()) : null;
    }

    public Iterable<Attribute> getProperties() {
        return Null.getInstance();
    }

    private Entity toEntity(Object object) {
        return new Entity(object instanceof Map ? new DynamicMapEntity(session, name, (Map) object) : new PojoEntity(session, name, object));
    }

    @Override
    public Iterator<Relatives> iterator() {
        return new HibernateEntityRelativesIterator();
    }

    private class HibernateEntityRelativesIterator implements Iterator<Relatives> {
        private Iterator<Object> iterator;

        public HibernateEntityRelativesIterator() {
            iterator = relatives.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Relatives next() {
            Object object = iterator.next();
            return new RelativesByEntity(toEntity(object));
        }

        public void remove() {
            iterator.remove();
        }
    }
}

