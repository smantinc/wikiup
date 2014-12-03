package org.wikiup.database.inf;

import org.wikiup.core.inf.Releasable;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool extends Releasable {
    public Connection getPooledConnection(ConnectionProvider provider) throws SQLException;
}
