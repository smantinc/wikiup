package org.wikiup.database.orm.inf;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Bindable;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Releasable;
import org.wikiup.database.orm.EntityRelatives;

public interface EntityModel extends Dictionary<Attribute>, PersistentOperation, Releasable, Bindable {
    public String getName();
    public EntityRelatives getRelatives(String name, Dictionary<?> props);
    public Iterable<Attribute> getAttributes();
}
