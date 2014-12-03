package org.wikiup.database.impl.cp;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.impl.ConnectionProxy;
import org.wikiup.database.inf.ConnectionPool;
import org.wikiup.database.inf.ConnectionProvider;
import org.wikiup.database.inf.PooledConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class DefaultConnectionPool implements ConnectionPool, DocumentAware {
    private List<PooledConnection> connections = new Vector<PooledConnection>();
    private int maxConnection;

    public void aware(Document data) {
        maxConnection = ValueUtil.toInteger(Documents.getDocumentValue(data, "max-connection", null), 0);
    }

    public Connection getPooledConnection(ConnectionProvider provider) throws SQLException {
        Connection conn = getUsableConnection();
        if(conn == null) {
            Assert.isTrue((maxConnection == 0 || maxConnection > connections.size()), IllegalStateException.class);
            conn = allocConnection(provider);
        }
        return conn;
    }

    private Connection getUsableConnection() throws SQLException {
        synchronized(connections) {
            Iterator<PooledConnection> iterator = connections.iterator();
            while(iterator.hasNext()) {
                PooledConnection conn = iterator.next();
                if(conn.isExpired())
                    iterator.remove();
                else if(!conn.isLocked()) {
                    conn.setLock(true);
                    return conn;
                }
            }
        }
        return null;
    }

    synchronized private Connection allocConnection(ConnectionProvider provider) throws SQLException {
        PooledConnection connection = new ConnectionProxy(provider.getConnection());
        connections.add(connection);
        return connection;
    }

    public void release() {
        Interfaces.releaseAll(connections);
        connections.clear();
    }
}
