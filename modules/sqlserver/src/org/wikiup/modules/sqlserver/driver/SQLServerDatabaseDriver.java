package org.wikiup.modules.sqlserver.driver;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.database.inf.DatabaseDriver;
import org.wikiup.database.orm.inf.SQLDialectInf;
import org.wikiup.modules.sqlserver.dialect.SQLServerDialect;

public class SQLServerDatabaseDriver implements DatabaseDriver, DocumentAware {
    private String url;

    public void setConnectionURL(String url) {
        this.url = url;
    }

    public Class getDriverClass() {
        return Interfaces.getClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    public SQLDialectInf getDialect() {
        return new SQLServerDialect();
    }

    public String getConnectionURL() {
        return url;
    }

    public void aware(Document desc) {
        url = Documents.getDocumentValue(desc, "url", null);
    }
}
