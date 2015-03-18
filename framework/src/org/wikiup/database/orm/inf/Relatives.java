package org.wikiup.database.orm.inf;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Dictionary;

public interface Relatives extends Dictionary<Attribute> {
    public Iterable<Attribute> getProperties();
    
    public interface OneToOne extends Relatives {
    }
    
    public interface OneToMany extends Relatives, Iterable<Relatives> {
    }
}
