package org.wikiup.modules.ibatis.entity;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.orm.EntityRelatives;
import org.wikiup.database.orm.imp.entity.NullEntity;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.modules.ibatis.IbatisEntityManager;
import org.wikiup.modules.ibatis.bindable.DataStore;
import org.wikiup.modules.ibatis.er.IbatisEntityRelatives;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IbatisEntity implements EntityModel, BeanFactory {
    private IbatisEntityManager entityManager;
    private EntityModel entity = new NullEntity();
    private String name;
    private DataStore dataStore = new DataStore();

    public IbatisEntity(IbatisEntityManager entityManager, String name) {
        this.entityManager = entityManager;
        this.name = name;
    }

    public void delete() {
        try {
            String id = StringUtil.connect(name, "delete", '.');
            entityManager.delete(id, getParameterObject(id));
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
    }

    public Attribute get(String name) {
        return dataStore.getStore() != null ? new IbatisEntityAttribute(name) : entity.get(name);
    }

    public Iterable<Attribute> getAttributes() {
        if(dataStore.getStore() != null) {
            List<Attribute> list = new ArrayList<Attribute>();
            for(String name : dataStore.getStore().keySet())
                list.add(new IbatisEntityAttribute(name));
            return list;
        }
        return entity.getAttributes();
    }

    public String getName() {
        return name;
    }

    public EntityRelatives getRelatives(String name, Getter<String> getter) {
        try {
            String id = StringUtil.connect(this.name, name, '.');
            List<Object> relatives = entityManager.queryForList(id, getParameterObject(id));
            return new IbatisEntityRelatives(name, relatives);
        } catch(SQLException ex) {
            Assert.fail(ex);
            return null;
        }
    }

    public void insert() {
        try {
            String id = StringUtil.connect(name, "insert", '.');
            entityManager.insert(id, getParameterObject(id));
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
    }

    public void release() {
        entity = null;
        dataStore = null;
    }

    public void select() {
        Object object = null;
        try {
            Object b = getParameterObject(name);
            object = entityManager.queryForObject(name, b);
            bind(object);
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
        Map<String, Object> map = Interfaces.cast(Map.class, object);
        if(object != null)
            if(map != null)
                entity = new MapEntity(name, map);
            else
                entity = new PojoEntity(name, object);
    }

    public void update() {
        try {
            String id = StringUtil.connect(name, "update", '.');
            entityManager.update(id, getParameterObject(id));
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
    }

    public void bind(Object obj) {
        dataStore.bind(obj);
    }

    private Object getParameterObject(String id) {
        Class<?> parameterClass = entityManager.getStatementParameterClass(id);
        return dataStore.query(parameterClass);
    }

    public <E> E query(Class<E> clazz) {
        return dataStore.query(clazz);
    }

    private class IbatisEntityAttribute implements Attribute {
        private String name;

        public IbatisEntityAttribute(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getObject() {
            Object val = dataStore.getStore() != null ? dataStore.getStore().get(name) : null;
            if(val != null)
                return val;
            if(dataStore.getStore() != null)
                select();
            Attribute attr = get(name);
            return attr != null ? attr.getObject() : null;
        }

        public void setObject(Object obj) {
            dataStore.getStore().put(name, obj);
        }

        @Override
        public String toString() {
            return ValueUtil.toString(getObject());
        }
    }
}
