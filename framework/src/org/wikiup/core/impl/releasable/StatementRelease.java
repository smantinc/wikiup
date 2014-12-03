package org.wikiup.core.impl.releasable;

import org.wikiup.core.inf.Releasable;
import org.wikiup.core.util.Assert;

import java.sql.SQLException;
import java.sql.Statement;

public class StatementRelease implements Releasable {
    private Statement statement;

    public StatementRelease(Statement stmt) {
        statement = stmt;
    }

    public void release() {
        try {
            statement.close();
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
    }
}
