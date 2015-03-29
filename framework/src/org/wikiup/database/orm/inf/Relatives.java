package org.wikiup.database.orm.inf;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Dictionary;

public interface Relatives extends Dictionary<Attribute> {
    Iterable<Attribute> getProperties();
    
    interface OneToOne extends Relatives {
    }
    
    interface OneToMany extends Relatives, Iterable<Relatives> {
    }
}
