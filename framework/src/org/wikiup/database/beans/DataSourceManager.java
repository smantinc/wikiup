package org.wikiup.database.beans;

import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.inf.DataSource;
import org.wikiup.database.inf.DatabaseDriver;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class DataSourceManager extends WikiupDynamicSingleton<DataSourceManager> implements Context<DataSource, Object>, DataSource, Iterable<String>, BeanContainer, Releasable {
    private Map<String, DataSource> dataSources;
    private DataSource dataSource;

    static public DataSourceManager getInstance() {
        return getInstance(DataSourceManager.class);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Map<String, DataSource> getDataSources() {
        return dataSources;
    }

    public void firstBuilt() {
        dataSources = new HashMap<String, DataSource>();
    }

    public DataSource get(String name) {
        return dataSources.get(name);
    }

    public void set(String name, Object obj) {
        DataSource ds = Interfaces.cast(DataSource.class, obj);
        if(ds != null) {
            dataSources.put(name, ds);
            setDataSource(ds);
        }
    }

    public DatabaseDriver getDatabaseDriver() {
        return getDataSource().getDatabaseDriver();
    }

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return getDataSource().getConnection(username, password);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return getDataSource().getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        getDataSource().setLogWriter(out);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        getDataSource().setLoginTimeout(seconds);
    }

    public int getLoginTimeout() throws SQLException {
        return getDataSource().getLoginTimeout();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getDataSource().getParentLogger();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getDataSource().unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getDataSource().isWrapperFor(iface);
    }

    public Iterator<String> iterator() {
        return dataSources.keySet().iterator();
    }

    public <E> E query(Class<E> clazz) {
        return Interfaces.getModelContainer(getDataSource()).query(clazz);
    }

    public void release() {
        Interfaces.releaseAll(dataSources.values());
    }
}
