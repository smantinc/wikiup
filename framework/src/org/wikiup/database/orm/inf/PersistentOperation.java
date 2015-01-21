package org.wikiup.database.orm.inf;

import org.wikiup.database.exception.InsufficientPrimaryKeys;
import org.wikiup.database.exception.RecordNotFoundException;

public interface PersistentOperation {
    public void select() throws RecordNotFoundException, InsufficientPrimaryKeys;

    public void update();

    public void delete();

    public void insert();
}
