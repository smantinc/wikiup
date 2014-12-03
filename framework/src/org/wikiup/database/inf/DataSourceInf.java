package org.wikiup.database.inf;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface DataSourceInf extends DataSource {
    public DatabaseDriver getDatabaseDriver();

    //for JDK5 compatibility
    public <T> T unwrap(Class<T> iface) throws SQLException;

    public boolean isWrapperFor(Class<?> iface) throws SQLException;
}
