package org.wikiup.database.impl.datasource;

import org.wikiup.Wikiup;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;

import javax.sql.DataSource;

public class WndiDataSource extends DataSourceWrapper implements DocumentAware {
    private String wndiName;

    public void aware(Document desc) {
        wndiName = Documents.getDocumentValue(desc, "wndi-name", null);
        Assert.notNull(wndiName, IllegalStateException.class, "Missing wndi-name property");
    }

    @Override
    public DataSource getDataSource() {
        if(dataSource == null)
            dataSource = Wikiup.getInstance().get(DataSource.class, wndiName);
        return Assert.notNull(dataSource, IllegalStateException.class, "No WNDI DataSource Context");
    }
}
