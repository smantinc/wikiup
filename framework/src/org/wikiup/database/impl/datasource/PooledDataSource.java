package org.wikiup.database.impl.datasource;

import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.util.Documents;
import org.wikiup.database.impl.DataSourceConnectionProvider;
import org.wikiup.database.impl.DataSourceWithAuthConnectionProvider;
import org.wikiup.database.inf.ConnectionPool;
import org.wikiup.database.inf.DataSourceInf;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class PooledDataSource extends DataSourceWrapper implements DocumentAware, Releasable {
    private ConnectionPool connectionPool;
    private String wndName;

    public Connection getConnection(String username, String password) throws SQLException {
        return connectionPool.getPooledConnection(new DataSourceWithAuthConnectionProvider(getDataSource(), username, password));
    }

    public Connection getConnection() throws SQLException {
        return connectionPool.getPooledConnection(new DataSourceConnectionProvider(getDataSource()));
    }

    public void aware(Document desc) {
        Document node = desc.getChild("connection-pool");
        wndName = Documents.getDocumentValue(desc, "data-source", null);
        connectionPool = Wikiup.build(ConnectionPool.class, node, node);
    }

    @Override
    public DataSource getDataSource() {
        DataSource ds = super.getDataSource();
        if(ds == null)
            setDataSource(ds = Wikiup.getInstance().get(DataSourceInf.class, wndName));
        return ds;
    }

    public void release() {
        if(connectionPool != null)
            connectionPool.release();
    }
}
