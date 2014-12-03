package org.wikiup.modules.dbcp;

import org.wikiup.core.util.Interfaces;
import org.wikiup.database.beans.DataSourceManager;
import org.wikiup.database.impl.datasource.PooledDataSource;
import org.wikiup.database.inf.DataSourceInf;

import java.util.HashMap;
import java.util.Map;

public class DbcpDataSources extends DataSourceManager {
    private Map<String, DataSourceInf> unpooledDataSources;

    @Override
    public void firstBuilt() {
        super.firstBuilt();
        unpooledDataSources = new HashMap<String, DataSourceInf>();
    }

    @Override
    public void cloneFrom(DataSourceManager instance) {
        cloneFrom(this, getClass().getSuperclass());
        DbcpDataSources dbcp = Interfaces.cast(DbcpDataSources.class, instance);
        if(dbcp != null)
            unpooledDataSources = dbcp.unpooledDataSources;
    }

    @Override
    public void set(String name, Object obj) {
        if(!(obj instanceof PooledDataSource)) {
            DataSourceInf ds = Interfaces.cast(DataSourceInf.class, obj);
            if(ds != null) {
                if(!isPooled(obj)) {
                    name = "dbcp(" + name + ")";
                    obj = new DbcpPooledDataSource(ds);
                }
                super.set(name, obj);
                if(!isPooled(obj))
                    unpooledDataSources.put(name, ds);
            }
        }
    }

    @Override
    public void setDataSource(DataSourceInf dataSource) {
        if(!isPooled(dataSource))
            super.setDataSource(new DbcpPooledDataSource(dataSource));
        else if(dataSource instanceof DbcpPooledDataSource)
            super.setDataSource(dataSource);
    }

    private boolean isPooled(Object ds) {
        return ds instanceof PooledDataSource || ds instanceof DbcpPooledDataSource;
    }
}