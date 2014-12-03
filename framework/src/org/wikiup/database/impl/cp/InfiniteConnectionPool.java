package org.wikiup.database.impl.cp;

import org.wikiup.database.inf.ConnectionPool;
import org.wikiup.database.inf.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;

public class InfiniteConnectionPool implements ConnectionPool {
    public Connection getPooledConnection(ConnectionProvider provider) throws SQLException {
        return provider.getConnection();
    }

    public void release() {
    }
}
