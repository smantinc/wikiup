package org.wikiup.servlet.impl.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.wikiup.core.Constants;
import org.wikiup.core.impl.dictionary.StackDictionary;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.orm.Entity;
import org.wikiup.database.orm.imp.entity.NullEntity;
import org.wikiup.database.util.EntityPath;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.exception.ContextObjectNotExistException;
import org.wikiup.servlet.impl.document.InterceptableDocument;
import org.wikiup.servlet.inf.ProcessorContext;

public class EntityProcessorContext implements ProcessorContext, DocumentAware, Iterable<String> {
    private ServletProcessorContext context;
    private List<String> entityNames = new ArrayList<String>();

    private EntityProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    private static Entity getEntity(ServletProcessorContext context, String name) {
        Entity entity = getEntityFromContext(context, name);
        return entity == null ? context.getEntity(name) : entity;
    }

    private static Entity getEntityFromContext(ServletProcessorContext context, String name) {
        Entity entity = Interfaces.cast(Entity.class, context.getAttribute(name));
        try {
            if(entity != null && entity.isDirty())
                entity.select();
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        return entity;
    }

    @Override
    public Object get(String name) {
        EntityPath ePath = new EntityPath(name);
        String eName = ePath.getEntityName();
        Assert.isTrue(eName != null, ContextObjectNotExistException.class, name);
        Entity entity = getEntity(context, eName);
        Assert.isTrue(entity != null, ContextObjectNotExistException.class, name);
        ePath.setEntity(entity);
        return name.equals(eName) ? entity : ePath.get();
    }

    private void initContextEntity(ServletProcessorContext context, Document node) {
        Entity entity = context.getEntity(context.getContextAttribute(node, Constants.Attributes.ENTITY_NAME));
        String name = Documents.getId(node);
        boolean ignore = Documents.getAttributeBooleanValue(node, "ignore-exceptions", false);
        try {
            context.setAttribute(name, entity);
            entityNames.add(name);
            Dictionaries.setProperties(node, entity, new StackDictionary<Object>().append(new DictionaryImpl(context), context));
        } catch(Exception ex) {
            if(ignore)
                context.setAttribute(name, new NullEntity());
            else
                Assert.fail(ex);
        }
    }

    public void aware(Document desc) {
        Document doc = new InterceptableDocument(context, desc);
        for(Document node : doc.getChildren())
            initContextEntity(context, node);
    }

    public Iterator<String> iterator() {
        return entityNames.iterator();
    }

    private static class DictionaryImpl implements Dictionary<Object> {
        private ServletProcessorContext context;
        
        private DictionaryImpl(ServletProcessorContext context) {
            this.context = context;
        }
        
        @Override
        public Object get(String name) {
            EntityPath ePath = new EntityPath(name);
            String eName = ePath.getEntityName();
            Entity entity = getEntityFromContext(context, eName);
            if(entity == null)
                return null;
            ePath.setEntity(entity);
            return name.equals(eName) ? entity : ePath.get();
        }
    }

    public static final class WIRABLE implements Wirable<EntityProcessorContext, ServletProcessorContext> {
        @Override
        public EntityProcessorContext wire(ServletProcessorContext context) {
            return new EntityProcessorContext(context);
        }
    }
}
