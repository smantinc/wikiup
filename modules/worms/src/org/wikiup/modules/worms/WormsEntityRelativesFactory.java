package org.wikiup.modules.worms;

import org.wikiup.core.Constants;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.impl.factory.FactoryByClass;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.modules.worms.imp.relatives.EntityRelatives;

public class WormsEntityRelativesFactory extends WikiupDynamicSingleton<WormsEntityRelativesFactory> implements DocumentAware {
    private FactoryByClass<EntityRelatives> factory;

    static public WormsEntityRelativesFactory getInstance() {
        return getInstance(WormsEntityRelativesFactory.class);
    }

    public WormsEntityRelativesFactory() {
    }

    public void firstBuilt() {
    }

    public org.wikiup.database.orm.EntityRelatives buildEntityRelatives(
            Document desc, WormsEntity origin, Getter<?> getter) {
        String type = Documents.getAttributeValue(desc, Constants.Attributes.TYPE, null);
        EntityRelatives relatives = factory.build(type);
        Assert.notNull(relatives, AttributeException.class, relatives != null ? null : "Entity " + origin.getName() + "'s " + Documents.getId(desc) + " relation has no type \"" + type + "\"");
        relatives.init(desc, origin, getter);
        return relatives;
    }

    @Override
    public void aware(Document desc) {
        factory = new FactoryByClass<EntityRelatives>(desc);
    }
}
