package org.wikiup.modules.dbcp;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DataSourceConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.wikiup.Wikiup;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.impl.datasource.DataSourceWrapper;
import org.wikiup.database.inf.DataSource;
import org.wikiup.database.inf.DatabaseDriver;

public class DbcpPooledDataSource extends DataSourceWrapper {
    private PoolableConnectionFactory poolableFactory;
    private javax.sql.DataSource orgDataSource;

    public DbcpPooledDataSource(DataSource dataSource) {
        setUnpooledDataSource(dataSource);
    }

    @Override
    public javax.sql.DataSource getDataSource() {
        if(dataSource == null) {
            DbcpDataSources manager = Wikiup.getModel(DbcpDataSources.class);
            dataSource = manager.getDataSource();
            if(!(dataSource instanceof DbcpPooledDataSource))
                setUnpooledDataSource(dataSource);
        }
        return dataSource;
    }

    @Override
    public DatabaseDriver getDatabaseDriver() {
        DataSource ds = Interfaces.cast(DataSource.class, orgDataSource);
        return ds != null ? ds.getDatabaseDriver() : null;
    }

    public void setUnpooledDataSource(javax.sql.DataSource dataSource) {
        GenericObjectPool pool = new GenericObjectPool(null);

        ConnectionFactory factory = new DataSourceConnectionFactory(dataSource);
        poolableFactory = new PoolableConnectionFactory(factory, pool, null, null, false, true);
        orgDataSource = dataSource;

        this.dataSource = new PoolingDataSource(pool);
    }

    @Override
    public <E> E query(Class<E> clazz) {
        return Interfaces.cast(clazz, javax.sql.DataSource.class.isAssignableFrom(clazz) ? orgDataSource : this);
    }
}
