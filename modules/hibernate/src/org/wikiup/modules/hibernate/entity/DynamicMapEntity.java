package org.wikiup.modules.hibernate.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.exception.InsufficientPrimaryKeys;
import org.wikiup.database.exception.RecordNotFoundException;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.database.orm.inf.Relatives;
import org.wikiup.modules.hibernate.HibernateEntityManager;
import org.wikiup.modules.hibernate.er.CollectionEntityRelatives;

public class DynamicMapEntity implements EntityModel {
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Relatives getRelatives(String name, Dictionary<?> parameters) {
        Query query = session.createQuery("from " + this.name);
        query.setFirstResult(ValueUtil.toInteger(map.get("offset"), 0));
        query.setMaxResults(ValueUtil.toInteger(map.get("limit"), 20));
        List<Object> list = query.list();
        return new CollectionEntityRelatives(session, name, list);
    }

    @Override
    public Iterable<Attribute> getAttributes() {
        List<Attribute> list = new ArrayList<Attribute>();
        for(String name : map.keySet())
            list.add(new DynamicMapEntityAttribute(name));
        return list;
    }

    @Override
    public void bind(Object object) {
    }

    @Override
    public Attribute get(String name) {
        if(propertyNames == null || propertyNames.contains(name))
            return new DynamicMapEntityAttribute(name);
        throw new AttributeException(this.name, name);
    }

    @Override
    public void select() throws RecordNotFoundException, InsufficientPrimaryKeys {
        map = (Map<String, Object>) session.load(name, metadata.getIdentifier(map, session.getEntityMode()));
    }

    @Override
    public void update() {
        session.update(name, map);
        session.flush();
    }

    @Override
    public void delete() {
        session.delete(name, map);
        session.flush();
    }

    @Override
    public void insert() {
        session.save(name, map);
        session.flush();
    }

    @Override
    public void release() {
        session.close();
    }

    private class DynamicMapEntityAttribute implements Attribute {
        private String name;

        public DynamicMapEntityAttribute(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
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

        @Override
        public void setObject(Object obj) {
            map.put(name, obj);
        }

        @Override
        public String toString() {
            return ValueUtil.toString(getObject());
        }
    }
}