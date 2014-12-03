package org.wikiup.database.orm.util;

import org.wikiup.core.util.Assert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLUtil {
    static public int getResultSetSize(ResultSet result) throws SQLException {
        int ret, index = result.getRow();
        result.last();
        ret = result.getRow();
        result.absolute(index);
        return ret;
    }

    static public void sqlExecute(Connection conn, String cmd) throws
            SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute(cmd);
        stmt.close();
    }

    static public void sqlExecute(Connection conn, String[] cmds) throws
            SQLException {
        Statement stmt = conn.createStatement();
        int loop;
        for(loop = 0; loop < cmds.length; loop++)
            stmt.execute(cmds[loop]);
        stmt.close();
    }

    static public ResultSet sqlQuery(Connection conn, String sql) {
        try {
            return conn.createStatement().executeQuery(sql);
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
        return null;
    }

    static public ResultSet sqlQuery(Connection conn, String sql, int rType, int cType) throws
            SQLException {
        return conn.createStatement(rType, cType).executeQuery(sql);
    }

    static public void sqlExecute(Connection conn, SQLStatement stmt) {
        PreparedStatement statment = stmt.getPreparedStatement(conn);
        try {
            statment.executeUpdate();
            statment.close();
        } catch(Exception ex) {
            Assert.fail(new SQLException(ex.getMessage() + "\r\n" + stmt.getSQL().toString()));
        }
    }

    static public ResultSet sqlQuery(Connection conn, SQLStatement stmt, int rType, int cType) throws SQLException {
        return query(stmt.getPreparedStatement(conn, rType, cType));
    }

    static public ResultSet sqlQuery(Connection conn, SQLStatement stmt) {
        return query(stmt.getPreparedStatement(conn));
    }

    static public ResultSet query(PreparedStatement stmt) {
        try {
            return stmt.executeQuery();
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
        return null;
    }
}
