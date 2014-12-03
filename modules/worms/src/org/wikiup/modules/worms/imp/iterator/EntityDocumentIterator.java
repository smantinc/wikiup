package org.wikiup.modules.worms.imp.iterator;

import org.wikiup.core.inf.Document;
import org.wikiup.database.orm.util.EntityDocument;
import org.wikiup.modules.worms.WormsEntity;

import java.util.Iterator;

public class EntityDocumentIterator implements Iterator<Document> {
    private WormsEntity entity;
    private Iterator<WormsEntity> iterator;
    private Document document;

    public EntityDocumentIterator(WormsEntity entity) {
        this.entity = entity;
        iterator = entity.iterator();
        document = new EntityDocument(entity);
        document.setName("relative");
    }

    public boolean hasNext() {
        return !entity.isEmpty() && iterator.hasNext();
    }

    public Document next() {
        iterator.next();
        return document;
    }

    public void remove() {
    }
}
