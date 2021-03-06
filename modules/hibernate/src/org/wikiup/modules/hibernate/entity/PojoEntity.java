package org.wikiup.modules.hibernate.entity;

import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Wrapper;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.exception.InsufficientPrimaryKeys;
import org.wikiup.database.exception.RecordNotFoundException;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.database.orm.inf.Relatives;
import org.wikiup.modules.hibernate.HibernateEntityManager;

public class PojoEntity implements Wrapper<Object>, EntityModel {
    private Object pojo;
    private Class pojoClass;
    private Session session;
    private String name;
    private ClassMetadata metadata;

    public PojoEntity(Session session, String entityName, Object pojo) {
        this(session, entityName);
        this.pojo = pojo;
    }

    public PojoEntity(Session session, String entityName) {
        String clazzName = ValueUtil.toString(HibernateEntityManager.getInstance().getConfiguration().getImports().get(entityName));
        this.session = session;
        this.name = entityName;
        pojoClass = Interfaces.getClass(clazzName);
        metadata = HibernateEntityManager.getInstance().getSessionFactory().getClassMetadata(pojoClass);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Relatives getRelatives(String name, Dictionary<?> dictionary) {
        return null;
    }

    @Override
    public Iterable<Attribute> getAttributes() {
        return null;
    }

    @Override
    public void bind(Object object) {
        pojo = object;
    }

    @Override
    public Attribute get(String name) {
        return new BeanProperty(wrapped(), name);
    }

    @Override
    public void select() throws RecordNotFoundException, InsufficientPrimaryKeys {
        pojo = session.load(name, metadata.getIdentifier(pojo, session.getEntityMode()));
    }

    @Override
    public void update() {
        session.update(name, pojo);
    }

    @Override
    public void delete() {
        session.delete(name, pojo);
    }

    @Override
    public void insert() {
        session.persist(name, wrapped());
    }

    @Override
    public void release() {
        session.flush();
        session.close();
    }

    @Override
    public Object wrapped() {
        try {
            return pojo != null ? pojo : (pojo = pojoClass.newInstance());
        } catch(Exception e) {
            Assert.fail(e);
        }
        return null;
    }
}
