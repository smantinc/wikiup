package org.wikiup.database.impl.datasource;

import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.inf.DataSourceInf;
import org.wikiup.database.inf.DatabaseDriver;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class DataSourceWrapper implements DataSourceInf, BeanContainer {
    protected DataSource dataSource;

    public Connection getConnection(String username, String password) throws SQLException {
        return getDataSource().getConnection(username, password);
    }

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public DatabaseDriver getDatabaseDriver() {
        DataSourceInf ds = Interfaces.cast(DataSourceInf.class, getDataSource());
        return ds != null ? ds.getDatabaseDriver() : null;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return getDataSource().getLogWriter();
    }

    public int getLoginTimeout() throws SQLException {
        return getDataSource().getLoginTimeout();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        try {
            return (Logger) Interfaces.invoke(getDataSource(), "getParentLogger");
        } catch(NoSuchMethodException e) {
            throw new SQLFeatureNotSupportedException();
        }
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        getDataSource().setLogWriter(out);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        getDataSource().setLoginTimeout(seconds);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public <E> E query(Class<E> clazz) {
        return Interfaces.cast(clazz, DataSource.class.isAssignableFrom(clazz) ? getDataSource() : this);
    }
}
