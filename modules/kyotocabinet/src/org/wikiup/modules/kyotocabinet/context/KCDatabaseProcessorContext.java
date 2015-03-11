package org.wikiup.modules.kyotocabinet.context;

import kyotocabinet.DB;
import org.wikiup.core.Wikiup;
import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.modules.kyotocabinet.KCDatabaseManager;
import org.wikiup.servlet.inf.ProcessorContext;

public class KCDatabaseProcessorContext implements ProcessorContext, Dictionary.Mutable<String>, DocumentAware {
    private DB database;

    @Override
    public Object get(String name) {
        return database.get(name);
    }

    @Override
    public void set(String name, String value) {
        database.set(name, value);
    }

    @Override
    public void aware(Document doc) {
        String dbname = Documents.ensureAttributeValue(doc, "database-name");
        database = Wikiup.getModel(KCDatabaseManager.class).get(dbname);
        Assert.notNull(database, AttributeException.class, "kyotocabinet", dbname);
    }
}
