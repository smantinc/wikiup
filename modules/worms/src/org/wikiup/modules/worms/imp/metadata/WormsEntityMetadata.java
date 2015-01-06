package org.wikiup.modules.worms.imp.metadata;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.orm.FieldMetadata;
import org.wikiup.database.orm.inf.EntityMetadata;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.modules.worms.WormsEntityManager;
import org.wikiup.modules.worms.imp.FieldProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WormsEntityMetadata implements EntityMetadata, BeanContainer {
    public Document description;

    public WormsEntityMetadata extendz;
    public Map<String, Document> relations = new HashMap<String, Document>();

    private boolean merged = false;

    public WormsEntityMetadata(Document description) {
        this.description = description;
    }

    public Document getDescription() {
        if(!merged && extendz != null) {
            extendz.getDescription();
            Documents.merge(description.getChild("entity"), extendz.description.getChild("entity"));
        }
        merged = true;
        return description;
    }

    public Iterable<FieldMetadata> getProperties() {
        EntityModel entity = WormsEntityManager.getInstance().getEntityModel(getName());
        List<FieldMetadata> fields = new ArrayList<FieldMetadata>();
        for(Attribute attr : entity.getAttributes()) {
            FieldProperty fp = Interfaces.cast(FieldProperty.class, attr);
            if(fp != null) {
                FieldMetadata fm = new FieldMetadata();
                fm.setFieldName(fp.getFieldName());
                fm.setIdentity(fp.isIndexKey());
                fm.setPrimaryKey(fp.isPrimaryKey());
                fm.setFieldType(0);
                fields.add(fm);
            }
        }
        entity.release();
        return fields;
    }

    public String getTable() {
        return getEntityAttribute("table", null);
    }

    public String getSchema() {
        return getEntityAttribute("schema", null);
    }

    public String getCatalog() {
        return getEntityAttribute("catalog", null);
    }

    public String getLocation() {
        String table = getTable();
        String schema = ValueUtil.toString(getSchema(), "");
        String catalog = ValueUtil.toString(getCatalog(), "");
        return StringUtil.trim(catalog + '.' + schema + '.' + table, ".");
    }

    public String getName() {
        return Documents.getAttributeValue(description, "name", null);
    }

    private String getEntityAttribute(String name, String defValue) {
        Document entity = description.getChild("entity");
        return Documents.getAttributeValue(entity != null ? entity : description, name, defValue);
    }

    public <E> E query(Class<E> clazz) {
        return Interfaces.findAssignable(clazz, description, this);
    }
}
