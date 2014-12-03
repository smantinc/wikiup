package org.wikiup.database.impl;

import org.wikiup.database.inf.ConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceWithAuthConnectionProvider implements ConnectionProvider {
    private DataSource dataSource;
    private String name;
    private String password;

    public DataSourceWithAuthConnectionProvider(DataSource ds, String name, String password) {
        dataSource = ds;
        this.name = name;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection(name, password);
    }
}
