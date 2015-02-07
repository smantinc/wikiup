package org.wikiup.modules.hibernate.entity;

import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.exception.InsufficientPrimaryKeys;
import org.wikiup.database.exception.RecordNotFoundException;
import org.wikiup.database.orm.EntityRelatives;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.modules.hibernate.HibernateEntityManager;

public class PojoEntity implements EntityModel, BeanContainer, Releasable {
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

    public String getName() {
        return name;
    }

    public EntityRelatives getRelatives(String name, Getter<?> getter) {
        return null;
    }

    public Iterable<Attribute> getAttributes() {
        return null;
    }

    public void bind(Object object) {
        pojo = object;
    }

    public Attribute get(String name) {
        return new BeanProperty(getPojoInstance(), name);
    }

    public void select() throws RecordNotFoundException, InsufficientPrimaryKeys {
        pojo = session.load(name, metadata.getIdentifier(pojo, session.getEntityMode()));
    }

    public void update() {
        session.update(name, pojo);
    }

    public void delete() {
        session.delete(name, pojo);
    }

    public void insert() {
        session.persist(name, getPojoInstance());
    }

    public void release() {
        session.flush();
        session.close();
    }

    public <E> E query(Class<E> clazz) {
        return Interfaces.cast(clazz, getPojoInstance());
    }

    public Object getPojoInstance() {
        try {
            return pojo != null ? pojo : (pojo = pojoClass.newInstance());
        } catch(Exception e) {
            Assert.fail(e);
        }
        return null;
    }
}
