package org.wikiup.modules.oracle.driver;

import org.wikiup.core.bean.WikiupNamingDirectory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.inf.DatabaseDriver;
import org.wikiup.database.orm.inf.SQLDialectInf;
import org.wikiup.modules.oracle.dialect.OracleDialect;

public class OracleDatabaseDriver implements DatabaseDriver, DocumentAware {
    private String url;

    public String getConnectionURL() {
        return url;
    }

    public void setConnectionURL(String url) {
        this.url = url;
    }

    public Class getDriverClass() {
        return Interfaces.getClass("oracle.jdbc.driver.OracleDriver");
    }

    public SQLDialectInf getDialect() {
        return new OracleDialect();
    }

    public void aware(Document desc) {
        url = StringUtil.evaluateEL(Documents.getDocumentValue(desc, "url"), WikiupNamingDirectory.getInstance());
    }
}
