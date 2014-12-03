package org.wikiup.modules.hibernate;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.Resource;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.orm.FieldMetadata;
import org.wikiup.database.orm.inf.EntityManager;
import org.wikiup.database.orm.inf.EntityMetadata;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.modules.hibernate.entity.DynamicMapEntity;
import org.wikiup.modules.hibernate.entity.PojoEntity;
import org.wikiup.modules.hibernate.meta.HibernateEntityMetadata;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class HibernateEntityManager extends WikiupDynamicSingleton<HibernateEntityManager> implements EntityManager, DocumentAware, Releasable {
    private Configuration configuration;
    private Document describe;
    private SessionFactory sessionFactory;

    private Set<Resource> resources = new HashSet<Resource>();

    static public HibernateEntityManager getInstance() {
        return getInstance(HibernateEntityManager.class);
    }

    public EntityMetadata getEntityMetadata(String name) {
        return null;
    }

    public void loadResource(Resource res) {
        configuration.addInputStream(res.open());
        resources.add(res);
    }

    public EntityModel getEntityModel(String name) {
        Session session = openSession();
        EntityMode em = session.getEntityMode();
        if(em.equals(EntityMode.MAP))
            return new DynamicMapEntity(session, name);
        else if(em.equals(EntityMode.POJO))
            return new PojoEntity(session, name);
        return null;
    }

    @Override
    public void aware(Document desc) {
        describe = desc.getChild("describe");
    }

    public void firstBuilt() {
        configuration = new Configuration();
    }

    public Document describe(EntityMetadata metadata) {
        Document root = Documents.create("hibernate-mapping");
        Document entity = root.addChild("class");
        String schema = metadata.getSchema();
        String catalog = metadata.getCatalog();
        if(StringUtil.isNotEmpty(catalog))
            Documents.setAttributeValue(root, "catalog", catalog);
        if(StringUtil.isNotEmpty(schema))
            Documents.setAttributeValue(root, "schema", schema);
        Documents.setAttributeValue(entity, "table", metadata.getTable());
        Documents.setAttributeValue(entity, "entity-name", metadata.getName());
        for(FieldMetadata fm : metadata.getProperties())
            if(fm.isPrimaryKey())
                appendEntityProperty(entity, fm);
        for(FieldMetadata fm : metadata.getProperties())
            if(!fm.isPrimaryKey())
                appendEntityProperty(entity, fm);
        return root;
    }

    private void appendEntityProperty(Document entity, FieldMetadata fm) {
        Document field = entity.addChild(fm.isPrimaryKey() ? "id" : "property");
        Documents.setAttributeValue(field, "name", fm.getName());
        Documents.setAttributeValue(field, "column", fm.getFieldName());
        Documents.setAttributeValue(field, "type", getFieldTypeString(fm.getFieldType()));
        if(fm.isPrimaryKey())
            Documents.setAttributeValue(field.addChild("generator"), "class", "native");
    }

    public Iterable<EntityMetadata> getEntityMetadatas() {
        List<EntityMetadata> lists = new LinkedList<EntityMetadata>();
        for(Resource res : resources)
            lists.add(new HibernateEntityMetadata(FileUtil.getFileName(res.getURI(), '/')));
        return lists;
    }

    public void buildSessionFactory() {
        this.sessionFactory = configuration.buildSessionFactory();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private String getFieldTypeString(int type) {
        Document doc = Documents.findMatchesChild(describe, "id", String.valueOf(type));
        return doc != null ? Documents.getAttributeValue(doc, "type", "string") : "string";
    }

    public void release() {
        if(sessionFactory != null)
            sessionFactory.close();
        configuration = null;
        sessionFactory = null;
        resources.clear();
    }
}
