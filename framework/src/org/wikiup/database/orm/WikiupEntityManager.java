package org.wikiup.database.orm;

import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.orm.inf.EntityManager;
import org.wikiup.database.orm.inf.EntityMetadata;
import org.wikiup.database.orm.inf.EntityModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WikiupEntityManager extends WikiupDynamicSingleton<WikiupEntityManager> implements EntityManager, Context<EntityManager, EntityManager>, Iterable<String> {
    private Map<String, EntityManager> managers;
    private EntityManager defaultManager;

    static public WikiupEntityManager getInstance() {
        return getInstance(WikiupEntityManager.class);
    }

    public EntityManager getDefaultEntityManager() {
        return defaultManager;
    }

    public void setDefaultEntityManager(EntityManager defaultManager) {
        this.defaultManager = defaultManager;
    }

    public Map<String, EntityManager> getEntityManagers() {
        return managers;
    }

    public Entity getEntity(String name) {
        return new Entity(getEntityModel(name));
    }

    public EntityModel getEntityModel(String name) {
        int idx = name.indexOf(':');
        String managerName = idx != -1 ? name.substring(0, idx) : null;
        String entityName = idx != -1 ? name.substring(idx + 1) : name;
        return getEntityManager(managerName).getEntityModel(entityName);
    }

    public EntityMetadata getEntityMetadata(String name) {
        int idx = name.indexOf(':');
        String managerName = idx != -1 ? name.substring(0, idx) : null;
        String entityName = idx != -1 ? name.substring(idx + 1) : name;
        return getEntityManager(managerName).getEntityMetadata(entityName);
    }

    public EntityManager getEntityManager(String name) {
        EntityManager manager = StringUtil.isEmpty(name) ? getDefaultEntityManager() : managers.get(name);
        Assert.notNull(manager, "WikiupEntityManager: " + name);
        return manager;
    }

    public void firstBuilt() {
        managers = new HashMap<String, EntityManager>();
    }

    public Document describe(EntityMetadata metadata) {
        return getDefaultEntityManager().describe(metadata);
    }

    public Iterable<EntityMetadata> getEntityMetadatas() {
        return getDefaultEntityManager().getEntityMetadatas();
    }

    public EntityManager get(String name) {
        return getEntityManager(name);
    }

    public void set(String name, EntityManager mgr) {
        managers.put(name, mgr);
        defaultManager = mgr;
    }

    public Iterator<String> iterator() {
        return managers.keySet().iterator();
    }
}
