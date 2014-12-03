package org.wikiup.database.impl;

import org.wikiup.core.impl.releasable.StatementRelease;
import org.wikiup.core.impl.releasable.TrashTin;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.inf.PooledConnection;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ConnectionProxy implements PooledConnection, Releasable {
    private boolean locked = true;
    private TrashTin trashTin = new TrashTin();

    private Connection conn;

    public ConnectionProxy(Connection conn) {
        this.conn = conn;
    }

    public void close() throws SQLException {
        locked = false;
        trashTin.release();
    }

    public boolean isClosed() throws SQLException {
        return conn.isClosed() || locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLock(boolean locked) {
        this.locked = locked;
    }

    public void release() {
        trashTin.release();
        try {
            conn.close();
        } catch(SQLException e) {
        }
    }

    public boolean isExpired() {
        try {
            return conn.isClosed();
        } catch(SQLException e) {
            return true;
        }
    }

    public Statement createStatement() throws SQLException {
        return collect(conn.createStatement());
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return collect(conn.prepareStatement(sql));
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return collect(conn.prepareCall(sql));
    }

    public String nativeSQL(String sql) throws SQLException {
        return conn.nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        conn.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return conn.getAutoCommit();
    }

    public void commit() throws SQLException {
        conn.commit();
    }

    public void rollback() throws SQLException {
        conn.rollback();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        conn.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        return conn.isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException {
        conn.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
        return conn.getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException {
        conn.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return conn.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        return conn.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        conn.clearWarnings();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return collect(conn.createStatement(resultSetType, resultSetConcurrency));
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return collect(conn.prepareStatement(sql, resultSetType, resultSetConcurrency));
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return collect(conn.prepareCall(sql, resultSetType, resultSetConcurrency));
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return conn.getTypeMap();
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        conn.setTypeMap(map);
    }

    public void setHoldability(int holdability) throws SQLException {
        conn.setHoldability(holdability);
    }

    public int getHoldability() throws SQLException {
        return conn.getHoldability();
    }

    public Savepoint setSavepoint() throws SQLException {
        return conn.setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return conn.setSavepoint(name);
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        conn.rollback(savepoint);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        conn.releaseSavepoint(savepoint);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return collect(conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return collect(conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return collect(conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return collect(conn.prepareStatement(sql, autoGeneratedKeys));
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return collect(conn.prepareStatement(sql, columnIndexes));
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return collect(conn.prepareStatement(sql, columnNames));
    }

    public Clob createClob() throws SQLException {
        return conn.createClob();
    }

    public Blob createBlob() throws SQLException {
        return conn.createBlob();
    }

    public NClob createNClob() throws SQLException {
        return conn.createNClob();
    }

    public SQLXML createSQLXML() throws SQLException {
        return conn.createSQLXML();
    }

    public boolean isValid(int timeout) throws SQLException {
        return conn.isValid(timeout);
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        conn.setClientInfo(name, value);
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        conn.setClientInfo(properties);
    }

    public String getClientInfo(String name) throws SQLException {
        return conn.getClientInfo(name);
    }

    public Properties getClientInfo() throws SQLException {
        return conn.getClientInfo();
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return conn.createArrayOf(typeName, elements);
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return conn.createStruct(typeName, attributes);
    }

    public void setSchema(String schema) throws SQLException {
        try {
            Interfaces.invoke(conn, "setSchema", schema);
        } catch(NoSuchMethodException e) {
        }
    }

    public String getSchema() throws SQLException {
        try {
            return (String) Interfaces.invoke(conn, "getSchema");
        } catch(NoSuchMethodException e) {
            return null;
        }
    }

    public void abort(Executor executor) throws SQLException {
        try {
            Interfaces.invoke(conn, "abort", executor);
        } catch(NoSuchMethodException e) {
        }
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        try {
            Interfaces.invoke(conn, "setNetworkTimeout", executor, milliseconds);
        } catch(NoSuchMethodException e) {
        }
    }

    public int getNetworkTimeout() throws SQLException {
        try {
            return (Integer) Interfaces.invoke(conn, "getNetworkTimeout");
        } catch(NoSuchMethodException e) {
            return 0;
        }
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return Interfaces.cast(iface, Interfaces.invoke(conn, "unwrap", iface));
        } catch(NoSuchMethodException e) {
            return null;
        }
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        try {
            return (Boolean) Interfaces.invoke(conn, "isWrapperFor", iface);
        } catch(NoSuchMethodException e) {
            return false;
        }
    }

    private <E extends Statement> E collect(E stmt) {
        trashTin.put(new StatementRelease(stmt));
        return stmt;
    }
}
