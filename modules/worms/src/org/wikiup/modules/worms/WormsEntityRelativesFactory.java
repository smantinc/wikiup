package org.wikiup.modules.worms;

import org.wikiup.core.Constants;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.impl.factory.FactoryImpl;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.database.orm.inf.Relatives;
import org.wikiup.modules.worms.imp.relatives.EntityRelatives;

public class WormsEntityRelativesFactory extends WikiupDynamicSingleton<WormsEntityRelativesFactory> implements DocumentAware {
    private FactoryImpl<EntityRelatives> factory;

    static public WormsEntityRelativesFactory getInstance() {
        return getInstance(WormsEntityRelativesFactory.class);
    }

    public WormsEntityRelativesFactory() {
    }

    public void firstBuilt() {
    }

    public Relatives buildEntityRelatives(
            Document desc, WormsEntity origin, Dictionary<?> dictionary) {
        String type = Documents.getAttributeValue(desc, Constants.Attributes.TYPE, null);
        EntityRelatives relatives = factory.build(type);
        Assert.notNull(relatives, AttributeException.class, relatives != null ? null : "Entity " + origin.getName() + "'s " + Documents.getId(desc), type);
        relatives.init(desc, origin, dictionary);
        return relatives;
    }

    @Override
    public void aware(Document desc) {
        factory = new FactoryImpl<EntityRelatives>(desc);
    }
}
