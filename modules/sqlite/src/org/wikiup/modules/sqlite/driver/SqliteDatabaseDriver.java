package org.wikiup.modules.sqlite.driver;

import org.wikiup.core.bean.WikiupNamingDirectory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.inf.DatabaseDriver;
import org.wikiup.database.orm.inf.SQLDialectInf;
import org.wikiup.modules.sqlite.dialect.SqliteDialect;

import java.io.IOException;

public class SqliteDatabaseDriver implements DatabaseDriver, DocumentAware {
    private String url;
    private SqliteDialect dialect = new SqliteDialect();

    public String getConnectionURL() {
        return url;
    }

    public void setConnectionURL(String url) {
        this.url = url;
    }

    public Class getDriverClass() {
        return Interfaces.getClass("org.sqlite.JDBC");
    }

    public SQLDialectInf getDialect() {
        return dialect;
    }

    public void aware(Document desc) {
        url = StringUtil.evaluateEL(Documents.getDocumentValue(desc, "url"), WikiupNamingDirectory.getInstance());
        try {
            FileUtil.touch(StringUtil.shrinkLeft(url, "jdbc:sqlite:"));
        } catch(IOException e) {
        }
    }
}
