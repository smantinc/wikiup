package org.wikiup.servlet.impl.context;

import org.wikiup.core.impl.getter.StackGetter;
import org.wikiup.core.impl.mp.DocumentModelProvider;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.orm.Entity;
import org.wikiup.database.orm.imp.entity.NullEntity;
import org.wikiup.database.util.EntityPath;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.exception.ContextObjectNotExistException;
import org.wikiup.servlet.impl.document.InterceptableDocument;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityProcessorContext implements ProcessorContext, ServletProcessorContextAware, DocumentAware, Iterable<String> {
    private ServletProcessorContext context;
    private List<String> entityNames = new ArrayList<String>();

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    public BeanFactory getModelContainer(String name, Getter<?> params) {
        Document doc;
        EntityPath ePath = new EntityPath(name);
        String eName = ePath.getEntityName();
        Entity entity = getEntity(context, eName);
        Assert.notNull(entity, ContextObjectNotExistException.class, name);
        ePath.setEntity(entity);
        doc = ePath.getDocument();
        Assert.notNull(doc, ContextObjectNotExistException.class, name);
        return new DocumentModelProvider(doc);
    }

    private Entity getEntity(ServletProcessorContext context, String name) {
        Entity entity = Interfaces.cast(Entity.class, context.getAttribute(name));
        try {
            if(entity != null && entity.isDirty())
                entity.select();
        } catch(Exception ex) {
            Assert.fail(ex);
        }
        return entity == null ? context.getEntity(name) : entity;
    }

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
        Entity entity = this.context.getEntity(context.getContextAttribute(node, "entity-name"));
        String name = Documents.getId(node);
        boolean ignore = Documents.getAttributeBooleanValue(node, "ignore-exceptions", false);
        try {
            context.setAttribute(name, entity);
            entityNames.add(name);
            ContextUtil.setProperties(node, entity, new StackGetter<Object>().append(this, this.context));
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
}
