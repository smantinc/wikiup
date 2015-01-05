package org.wikiup.modules.dbcp;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DataSourceConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.wikiup.core.Wikiup;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.impl.datasource.DataSourceWrapper;
import org.wikiup.database.inf.DataSourceInf;
import org.wikiup.database.inf.DatabaseDriver;

import javax.sql.DataSource;

public class DbcpPooledDataSource extends DataSourceWrapper {
    private PoolableConnectionFactory poolableFactory;
    private DataSource orgDataSource;

    public DbcpPooledDataSource(DataSourceInf dataSource) {
        setUnpooledDataSource(dataSource);
    }

    @Override
    public DataSource getDataSource() {
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
        DataSourceInf ds = Interfaces.cast(DataSourceInf.class, orgDataSource);
        return ds != null ? ds.getDatabaseDriver() : null;
    }

    public void setUnpooledDataSource(DataSource dataSource) {
        GenericObjectPool pool = new GenericObjectPool(null);

        ConnectionFactory factory = new DataSourceConnectionFactory(dataSource);
        poolableFactory = new PoolableConnectionFactory(factory, pool, null, null, false, true);
        orgDataSource = dataSource;

        this.dataSource = new PoolingDataSource(pool);
    }

    @Override
    public <E> E query(Class<E> clazz) {
        return Interfaces.cast(clazz, DataSource.class.isAssignableFrom(clazz) ? orgDataSource : this);
    }
}
