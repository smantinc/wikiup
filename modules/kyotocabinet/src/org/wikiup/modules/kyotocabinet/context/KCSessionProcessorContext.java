package org.wikiup.modules.kyotocabinet.context;

import kyotocabinet.DB;
import org.wikiup.core.Wikiup;
import org.wikiup.core.annotation.Property;
import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Assert;
import org.wikiup.modules.kyotocabinet.KCDatabaseManager;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;

public class KCSessionProcessorContext implements ProcessorContext, Dictionary.Mutable<String>, DocumentAware {
    private ServletProcessorContext context;
    private DB database;

    @Property
    private String sessionId;

    public KCSessionProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    @Override
    public Object get(String name) {
        return database.get(getKey(name));
    }

    @Override
    public void aware(Document doc) {
        String dbname = context.getContextAttribute(doc, "database-name");
        sessionId = context.getContextAttribute(doc, "session-id", null);
        database = Wikiup.getModel(KCDatabaseManager.class).get(dbname);
        Assert.notNull(database, AttributeException.class, "kyotocabinet", dbname);
    }

    @Override
    public void set(String name, String value) {
        String attrname = getKey(name);
        database.set(attrname, value);
    }

    private String getKey(String name) {
        return sessionId != null ? sessionId + "/" + name : name;
    }

    public static final class WIRABLE implements Wirable<KCSessionProcessorContext, ServletProcessorContext> {
        @Override
        public KCSessionProcessorContext wire(ServletProcessorContext context) {
            return new KCSessionProcessorContext(context);
        }
    }
}
