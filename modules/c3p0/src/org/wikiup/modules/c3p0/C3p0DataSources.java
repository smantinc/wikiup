package org.wikiup.modules.c3p0;

import org.wikiup.core.util.Interfaces;
import org.wikiup.database.beans.DataSourceManager;
import org.wikiup.database.impl.datasource.PooledDataSource;
import org.wikiup.database.inf.DataSource;

import java.util.HashMap;
import java.util.Map;

public class C3p0DataSources extends DataSourceManager {
    private Map<String, DataSource> unpooledDataSources;

    @Override
    public void firstBuilt() {
        super.firstBuilt();
        unpooledDataSources = new HashMap<String, DataSource>();
    }

    @Override
    public void cloneFrom(DataSourceManager instance) {
        cloneFrom(this, getClass().getSuperclass());
        C3p0DataSources c3p0 = Interfaces.cast(C3p0DataSources.class, instance);
        if(c3p0 != null)
            unpooledDataSources = c3p0.unpooledDataSources;
    }

    @Override
    public void set(String name, Object obj) {
        if(!(obj instanceof PooledDataSource)) {
            DataSource ds = Interfaces.cast(DataSource.class, obj);
            if(ds != null) {
                if(!isPooled(obj)) {
                    name = "c3p0(" + name + ")";
                    obj = new C3p0PooledDataSource(ds);
                }
                super.set(name, obj);
                if(!isPooled(obj))
                    unpooledDataSources.put(name, ds);
            }
        }
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        if(!isPooled(dataSource))
            super.setDataSource(new C3p0PooledDataSource(dataSource));
        else if(dataSource instanceof C3p0PooledDataSource)
            super.setDataSource(dataSource);
    }

    private boolean isPooled(Object ds) {
        return ds instanceof PooledDataSource || ds instanceof C3p0PooledDataSource;
    }
}
