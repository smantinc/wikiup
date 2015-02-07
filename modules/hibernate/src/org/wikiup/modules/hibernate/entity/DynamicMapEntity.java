package org.wikiup.modules.hibernate.entity;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.exception.InsufficientPrimaryKeys;
import org.wikiup.database.exception.RecordNotFoundException;
import org.wikiup.database.orm.EntityRelatives;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.modules.hibernate.HibernateEntityManager;
import org.wikiup.modules.hibernate.er.CollectionEntityRelatives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamicMapEntity implements EntityModel, Releasable {
    private Map<String, Object> map = new HashMap<String, Object>();
    private Set<String> propertyNames;
    private Session session;
    private String name;
    private ClassMetadata metadata;
    private boolean selected = false;

    public DynamicMapEntity(Session session, String entityName, Map map) {
        this(session, entityName);
        this.map = map;
    }

    public DynamicMapEntity(Session session, String entityName) {
        this.session = session;
        this.name = entityName;
        metadata = HibernateEntityManager.getInstance().getSessionFactory().getClassMetadata(entityName);
        if(metadata != null) {
            propertyNames = new HashSet<String>(Arrays.asList(metadata.getPropertyNames()));
            propertyNames.add("offset");
            propertyNames.add("limit");
        }
    }

    public String getName() {
        return name;
    }

    public EntityRelatives getRelatives(String name, Getter<?> parameters) {
        Query query = session.createQuery("from " + this.name);
        query.setFirstResult(ValueUtil.toInteger(map.get("offset"), 0));
        query.setMaxResults(ValueUtil.toInteger(map.get("limit"), 20));
        List<Object> list = query.list();
        return new CollectionEntityRelatives(session, name, list);
    }

    public Iterable<Attribute> getAttributes() {
        List<Attribute> list = new ArrayList<Attribute>();
        for(String name : map.keySet())
            list.add(new DynamicMapEntityAttribute(name));
        return list;
    }

    public void bind(Object object) {
    }

    public Attribute get(String name) {
        if(propertyNames == null || propertyNames.contains(name))
            return new DynamicMapEntityAttribute(name);
        throw new AttributeException(this.name, name);
    }

    public void select() throws RecordNotFoundException, InsufficientPrimaryKeys {
        map = (Map<String, Object>) session.load(name, metadata.getIdentifier(map, session.getEntityMode()));
    }

    public void update() {
        session.update(name, map);
        session.flush();
    }

    public void delete() {
        session.delete(name, map);
        session.flush();
    }

    public void insert() {
        session.save(name, map);
        session.flush();
    }

    public void release() {
        session.close();
    }

    private class DynamicMapEntityAttribute implements Attribute {
        private String name;

        public DynamicMapEntityAttribute(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getObject() {
            Object val = map.get(name);
            if(val != null)
                return val;
            if(!selected)
                select();
            selected = true;
            Attribute attr = get(name);
            return attr != null ? attr.getObject() : null;
        }

        public void setObject(Object obj) {
            map.put(name, obj);
        }

        @Override
        public String toString() {
            return ValueUtil.toString(getObject());
        }
    }
}