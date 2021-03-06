package org.wikiup.database.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.database.orm.inf.Relatives;

public class EntityPath {
    final static private Pattern PATH_PATTERN = Pattern.compile("([\\w\\d:._]+)(?:\\-([\\w_\\d]+))?([\\w\\/]+)?(?:\\[@([^\\]]+)\\])?");

    private String entityName;
    private String relativeName;
    private String pathName;
    private String propertyName;
    private EntityModel entityObject;
    private Relatives relatives = null;
    private Context<String, String> parameters = null;

    public EntityPath(EntityModel entity, String path, Context<String, String> accessor) {
        entityObject = entity;
        this.parameters = accessor;
        parsePath(path);
    }

    public EntityPath(String path) {
        parsePath(path);
    }

    public Object get() {
        Attribute attribute = null;
        if(propertyName != null) {
            Relatives relatives = getRelatives();
            if(relatives != null)
                attribute = relatives.get(propertyName);
            else
                attribute = entityObject.get(propertyName);
        }
        return attribute != null ? ValueUtil.toString(attribute) : getRelatives();
    }

    public Relatives getRelatives() {
        if(relatives == null)
            relatives = relativeName != null ? entityObject.getRelatives(relativeName, parameters) : null;
        return relatives;
    }

    public EntityModel getEntity() {
        return entityObject;
    }

    public void setEntity(EntityModel entity, Context accessor) {
        entityObject = entity;
        this.parameters = accessor;
    }

    public void setEntity(EntityModel entity) {
        entityObject = entity;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getRelativeName() {
        return relativeName;
    }

    private void parsePath(String path) {
        Matcher matcher = PATH_PATTERN.matcher(path);
        if(matcher.matches()) {
            entityName = matcher.group(1);
            relativeName = matcher.group(2);
            pathName = matcher.group(3);
            propertyName = matcher.group(4);
        }
    }
}
