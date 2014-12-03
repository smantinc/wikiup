package org.wikiup.database.inf;

import org.wikiup.core.inf.Expirable;
import org.wikiup.core.inf.Releasable;

import java.sql.Connection;

public interface PooledConnection extends Connection, Releasable, Expirable {
    public boolean isLocked();

    public void setLock(boolean locked);
}
