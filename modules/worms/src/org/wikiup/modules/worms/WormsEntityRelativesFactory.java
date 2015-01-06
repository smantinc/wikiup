package org.wikiup.modules.worms;

import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.impl.mf.ClassNameFactory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.modules.worms.imp.relatives.EntityRelatives;

public class WormsEntityRelativesFactory extends WikiupDynamicSingleton<WormsEntityRelativesFactory> implements DocumentAware {
    private ClassNameFactory factory;

    static public WormsEntityRelativesFactory getInstance() {
        return getInstance(WormsEntityRelativesFactory.class);
    }

    public WormsEntityRelativesFactory() {
        factory = new ClassNameFactory();
    }

    public void firstBuilt() {
    }

    public org.wikiup.database.orm.EntityRelatives buildEntityRelatives(
            Document desc, WormsEntity origin, Getter<?> getter) {
        String type = Documents.getAttributeValue(desc, "type", null);
        BeanContainer mc = factory.get(type);
        Assert.notNull(mc, AttributeException.class, mc != null ? null : "Entity " + origin.getName() + "'s " + Documents.getId(desc) + " relation has no type \"" + type + "\"");
        EntityRelatives relatives = mc.query(EntityRelatives.class);
        relatives.init(desc, origin, getter);
        return relatives;
    }

    @Override
    public void aware(Document desc) {
        factory.aware(desc);
    }
}
