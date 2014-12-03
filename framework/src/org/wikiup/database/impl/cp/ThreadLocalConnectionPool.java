package org.wikiup.database.impl.cp;

import org.wikiup.core.util.Interfaces;
import org.wikiup.database.impl.ConnectionProxy;
import org.wikiup.database.inf.ConnectionPool;
import org.wikiup.database.inf.ConnectionProvider;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ThreadLocalConnectionPool implements ConnectionPool {
    private ThreadLocal<Connection> threadLocalConnectionHolder = new ThreadLocal<Connection>();
    private Set<ThreadLocalConnectionContainer> contains = new HashSet<ThreadLocalConnectionContainer>();

    public Connection getPooledConnection(ConnectionProvider provider) throws SQLException {
        Connection connection = threadLocalConnectionHolder.get();
        if(connection == null) {
            connection = new ConnectionProxy(provider.getConnection());
            addToConnectionContainer(connection);
            threadLocalConnectionHolder.set(connection);
        }
        return connection;
    }

    public void release() {
        Interfaces.release(threadLocalConnectionHolder.get());
        threadLocalConnectionHolder.remove();
    }

    synchronized private void addToConnectionContainer(Connection connection) throws SQLException {
        Iterator<ThreadLocalConnectionContainer> iterator = contains.iterator();
        while(iterator.hasNext()) {
            ThreadLocalConnectionContainer c = iterator.next();
            if(c.polled())
                iterator.remove();
        }
        contains.add(new ThreadLocalConnectionContainer(connection));
    }

    private static class ThreadLocalConnectionContainer {
        private PhantomReference<Connection> pReference;
        private ReferenceQueue<Connection> rQueue;

        public ThreadLocalConnectionContainer(Connection connection) {
            rQueue = new ReferenceQueue<Connection>();
            pReference = new PhantomReference<Connection>(connection, rQueue);
        }

        public boolean polled() throws SQLException {
            Reference<? extends Connection> ref = rQueue != null ? rQueue.poll() : null;
            if(ref != null) {
                Connection conn = ref.get();
                pReference = null;
                rQueue = null;
                if(conn != null) {
                    conn.close();
                    Interfaces.release(conn);
                }
            }
            return pReference == null;
        }
    }
}