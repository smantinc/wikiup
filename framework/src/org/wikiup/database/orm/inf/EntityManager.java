package org.wikiup.database.orm.inf;

import org.wikiup.core.inf.Document;

public interface EntityManager {
    public EntityModel getEntityModel(String name);

    public EntityMetadata getEntityMetadata(String name);

    public Document describe(EntityMetadata metadata);

    public Iterable<EntityMetadata> getEntityMetadatas();
}