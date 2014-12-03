package org.wikiup.database.inf;

import org.wikiup.database.orm.inf.SQLDialectInf;

public interface DatabaseDriver {
    public String getConnectionURL();

    public void setConnectionURL(String url);

    public Class getDriverClass();

    public SQLDialectInf getDialect();
}
