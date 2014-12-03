package org.wikiup.database.inf;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public interface DataSourceInf extends DataSource {
    public DatabaseDriver getDatabaseDriver();

    //for JDK6 compatibility
    public <T> T unwrap(Class<T> iface) throws SQLException;
    public boolean isWrapperFor(Class<?> iface) throws SQLException;

    //for JDK7 compatibility
    public Logger getParentLogger() throws SQLFeatureNotSupportedException;
}
