package org.wikiup.modules.hibernate.er;

import org.hibernate.Session;
import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.attribute.AttributeImpl;
import org.wikiup.core.impl.document.Context2Document;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.database.orm.Entity;
import org.wikiup.database.orm.EntityRelatives;
import org.wikiup.modules.hibernate.entity.DynamicMapEntity;
import org.wikiup.modules.hibernate.entity.PojoEntity;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class CollectionEntityRelatives extends EntityRelatives {
    private Collection<Object> relatives;
    private String name;
    private Session session;

    public CollectionEntityRelatives(Session session, String name, Collection<Object> relatives) {
        this.session = session;
        this.name = name;
        this.relatives = relatives;
    }

    public Document addChild(String name) {
        return null;
    }

    public Attribute getAttribute(String name) {
        return "size".equals(name) ? new AttributeImpl(name, relatives.size()) : null;
    }

    public Iterable<Attribute> getAttributes() {
        for(Object obj : relatives) {
            Document doc = getContext2Document(obj);
            return doc.getAttributes();
        }
        return Null.getInstance();
    }

    public Document getChild(String name) {
        return null;
    }

    public Iterable<Document> getChildren() {
        return new Iterable<Document>() {
            public Iterator<Document> iterator() {
                return new HibernateEntityRelativesIterator();
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

    private Context2Document getContext2Document(Object object) {
        Entity entity = new Entity(object instanceof Map ? new DynamicMapEntity(session, name, (Map) object) : new PojoEntity(session, name, object));
        Context2Document doc = new Context2Document(entity, object instanceof Map ? ((Map) object).keySet() : null);
        return doc;
    }

    private class HibernateEntityRelativesIterator implements Iterator<Document> {
        private Iterator<Object> iterator;

        public HibernateEntityRelativesIterator() {
            iterator = relatives.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Document next() {
            Object object = iterator.next();
            Context2Document doc = getContext2Document(object);
            doc.setName("relative");
            return doc;
        }

        public void remove() {
            iterator.remove();
        }
    }
}

