package org.wikiup.database.impl;

import org.wikiup.database.inf.ConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceConnectionProvider implements ConnectionProvider {
    private DataSource dataSource;

    public DataSourceConnectionProvider(DataSource ds) {
        dataSource = ds;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
