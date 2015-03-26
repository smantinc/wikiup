package org.wikiup.database.impl.datasource;

import org.wikiup.Wikiup;
import org.wikiup.core.annotation.Property;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.inf.DataSource;
import org.wikiup.database.inf.DatabaseDriver;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class DriverManagerDataSource implements DataSource, DocumentAware {
    private int timeout = 0;
    private PrintWriter logger = null;

    private Document databaseDriver;
    private DatabaseDriver databaseDriverInstance = null;

    @Property
    private String user;
    @Property
    private String password;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(getDatabaseDriver().getConnectionURL(), username, password);
    }

    public Connection getConnection() throws SQLException {
        String url = getDatabaseDriver().getConnectionURL();
        return user != null && password != null ? DriverManager.getConnection(url, user, password) : DriverManager.getConnection(url);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return logger;
    }

    public int getLoginTimeout() throws SQLException {
        return timeout;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        logger = out;
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        timeout = seconds;
    }

    public void aware(Document desc) {
        databaseDriver = desc.getChild("database-driver");
        user = Documents.getDocumentValue(desc, "user", null);
        password = Documents.getDocumentValue(desc, "password", null);
    }

    public DatabaseDriver getDatabaseDriver() {
        if(databaseDriverInstance == null) {
            databaseDriverInstance = Wikiup.getInstance().getBean(DatabaseDriver.class, databaseDriver);
            try {
                databaseDriverInstance.getDriverClass().newInstance();
            } catch(Exception e) {
                Assert.fail(e);
            }
            Interfaces.initialize(databaseDriverInstance, databaseDriver);
        }
        return databaseDriverInstance;
    }

    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    public <T> T unwrap(Class<T> iface) {
        return null;
    }

}
