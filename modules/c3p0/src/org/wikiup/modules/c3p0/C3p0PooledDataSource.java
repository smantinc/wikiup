package org.wikiup.modules.c3p0;

import com.mchange.v2.c3p0.DataSources;
import org.wikiup.core.Wikiup;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.impl.datasource.DataSourceWrapper;
import org.wikiup.database.inf.DataSourceInf;
import org.wikiup.database.inf.DatabaseDriver;

import javax.sql.DataSource;
import java.sql.SQLException;

public class C3p0PooledDataSource extends DataSourceWrapper {
    private DataSource orgDataSource;

    public C3p0PooledDataSource(DataSourceInf dataSource) {
        setUnpooledDataSource(dataSource);
    }

    @Override
    public DataSource getDataSource() {
        if(dataSource == null) {
            C3p0DataSources manager = Wikiup.getModel(C3p0DataSources.class);
            dataSource = manager.getDataSource();
            if(!(dataSource instanceof C3p0DataSources))
                setUnpooledDataSource(dataSource);
        }
        return dataSource;
    }

    @Override
    public DatabaseDriver getDatabaseDriver() {
        DataSourceInf ds = Interfaces.cast(DataSourceInf.class, orgDataSource);
        return ds != null ? ds.getDatabaseDriver() : null;
    }

    public void setUnpooledDataSource(DataSource ds) {
        try {
            orgDataSource = ds;
            dataSource = DataSources.pooledDataSource(ds);
        } catch(SQLException e) {
            Assert.fail(e);
        }
    }

    @Override
    public <E> E query(Class<E> clazz) {
        return Interfaces.cast(clazz, DataSource.class.isAssignableFrom(clazz) ? orgDataSource : this);
    }
}
