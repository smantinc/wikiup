package org.wikiup.database.impl.datasource;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JndiDataSource extends DataSourceWrapper implements DocumentAware {
    public void aware(Document desc) {
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(Documents.getDocumentValue(desc, "jndi-name"));
            Assert.notNull(ds, IllegalStateException.class, "No JNDI DataSource Context");
            setDataSource(ds);
        } catch(NamingException ex1) {
            Assert.fail(ex1);
        }
    }
}
