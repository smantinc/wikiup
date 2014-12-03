package org.wikiup.database.orm.inf;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Bindable;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Releasable;
import org.wikiup.database.orm.EntityRelatives;

public interface EntityModel extends Getter<Attribute>, PersistentOperationInf, Releasable, Bindable {
    public String getName();

    public EntityRelatives getRelatives(String name, Getter<String> getter);

    public Iterable<Attribute> getAttributes();
}
