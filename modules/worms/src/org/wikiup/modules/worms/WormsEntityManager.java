package org.wikiup.modules.worms;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.impl.iterable.GenericCastIterable;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.inf.DataSourceInf;
import org.wikiup.database.orm.FieldMetadata;
import org.wikiup.database.orm.inf.EntityManager;
import org.wikiup.database.orm.inf.EntityMetadata;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.modules.worms.exception.EntityDescriptionNotFoundException;
import org.wikiup.modules.worms.imp.metadata.WormsEntityMetadata;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WormsEntityManager extends WikiupDynamicSingleton<WormsEntityManager> implements EntityManager, DocumentAware {
    private Map<String, WormsEntityMetadata> entities;

    private Document describe;

    public static WormsEntityManager getInstance() {
        return getInstance(WormsEntityManager.class);
    }

    private void loadRelatives() {
        for(WormsEntityMetadata node : entities.values())
            loadEntityRelatives(node, node.description);
    }

    private void updateHierary() {
        for(WormsEntityMetadata node : entities.values()) {
            String extendz = Documents.getAttributeValue(node.description, "extends", null);
            node.extendz = extendz != null ? entities.get(extendz) : null;
            Assert.isTrue(extendz == null || node.extendz != null, "Super entity '" + extendz + "' doesn't exists");
        }
    }

    private void loadEntities(Collection<Resource> resources) {
        for(Resource res : resources)
            loadEntity(res);
    }

    private void loadEntity(Resource rl) {
        loadEntity(Documents.loadFromResource(rl));
    }

    private void loadEntity(Document desc) {
        String name = Documents.getId(desc);
        WormsEntityMetadata node = new WormsEntityMetadata(desc);
        entities.put(name, node);
    }

    private void loadEntityRelatives(WormsEntityMetadata node, Document desc) {
        Document relatives = desc.getChild("relatives");
        if(relatives != null) {
            for(Document relation : relatives.getChildren()) {
                String forName = Documents.getAttributeValue(relation, "for", null);
                appendEntityRelatives(forName != null ? entities.get(forName) : node, relation);
            }
        }
    }

    private void appendEntityRelatives(WormsEntityMetadata node, Document relative) {
        node.relations.put(Documents.getAttributeValue(relative, "name"), relative);
    }

    public EntityModel getEntityModel(String name) {
        DataSourceInf ds = Wikiup.getModel(DataSourceInf.class);
        return getEntityInterface(name, ds, null);
    }

    public Document describe(EntityMetadata metadata) {
        Document root = Documents.create("root");
        Document entity = root.addChild("entity");
        Documents.setAttributeValue(root, "name", metadata.getName());
        Documents.setAttributeValue(entity, "table", metadata.getTable());
        if(metadata.getCatalog() != null)
            Documents.setAttributeValue(entity, "catalog", metadata.getCatalog());
        if(metadata.getSchema() != null)
            Documents.setAttributeValue(entity, "schema", metadata.getSchema());
        for(FieldMetadata fm : metadata.getProperties()) {
            Document item = entity.addChild("field");
            String fieldName = fm.getFieldName();
            String fieldType = getFieldTypeString(fm.getFieldType(), describe);
            Documents.setAttributeValue(item, "field-name", fieldName);
            Documents.setAttributeValue(item, "name", StringUtil.getCamelName(fieldName, '_'));
            if(fieldType != null)
                Documents.setAttributeValue(item, "field-type", fieldType);
            if(fm.isPrimaryKey())
                Documents.setAttributeValue(item, "primary-key", "true");
        }
        return root;
    }

    public Iterable<EntityMetadata> getEntityMetadatas() {
        return new GenericCastIterable<EntityMetadata>(entities.values());
    }

    public EntityModel getEntityInterface(String name, DataSourceInf dataSource, Connection conn) {
        WormsEntity entity = null;
        Document desc = Assert.notNull(getEntityDescription(name), EntityDescriptionNotFoundException.class, name);
        try {
            entity = new WormsEntity(dataSource, conn != null ? conn : dataSource.getConnection());
            entity.initialize(desc);
        } catch(SQLException e) {
            Assert.fail(e);
        }
        return entity;
    }

    private Document getEntityDescription(String name) {
        WormsEntityMetadata node = entities.get(name);
        return node != null ? node.getDescription() : null;
    }

    public Document getEntityRelativeDescription(String name, String relative) {
        return getEntityRelativeDescription(entities.get(name), relative);
    }

    public EntityMetadata getEntityMetadata(String name) {
        return entities.get(name);
    }

    private Document getEntityRelativeDescription(WormsEntityMetadata node, String name) {
        return node != null ? (node.relations.containsKey(name) ? node.relations.get(name) : getEntityRelativeDescription(node.extendz, name)) : null;
    }

    @Override
    public void aware(Document desc) {
        describe = desc.getChild("describe");
    }

    public void initialize(Set<Resource> resources) {
        loadEntities(resources);
        loadRelatives();
        updateHierary();
    }

    public void firstBuilt() {
        entities = new HashMap<String, WormsEntityMetadata>();
    }

    private String getFieldTypeString(int type, Document desc) {
        Document doc = Documents.findMatchesChild(desc, "id", String.valueOf(type));
        return doc != null ? Documents.getAttributeValue(doc, "type", null) : null;
    }
}
