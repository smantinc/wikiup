package org.wikiup.modules.hsqldb.driver;

import org.wikiup.framework.bean.WikiupNamingDirectory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.inf.DatabaseDriver;
import org.wikiup.database.orm.inf.SQLDialect;
import org.wikiup.modules.hsqldb.dialect.HSQLDialect;

public class HSQLDatabaseDriver implements DatabaseDriver, DocumentAware {
    private String url;
    private HSQLDialect dialect = new HSQLDialect();

    public String getConnectionURL() {
        return url;
    }

    public void setConnectionURL(String url) {
        this.url = url;
    }

    public Class getDriverClass() {
        return Interfaces.getClass("org.hsqldb.jdbcDriver");
    }

    public SQLDialect getDialect() {
        return dialect;
    }

    public void aware(Document desc) {
        url = StringUtil.evaluateEL(Documents.getDocumentValue(desc, "url"), WikiupNamingDirectory.getInstance());
    }
}
