package org.wikiup.modules.kyotocabinet;

import java.io.File;
import java.util.HashMap;

import kyotocabinet.DB;
import org.wikiup.core.Constants;
import org.wikiup.core.Wikiup;
import org.wikiup.framework.bean.WikiupDynamicSingleton;
import org.wikiup.framework.bean.WikiupNamingDirectory;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;

public class KCDatabaseManager extends WikiupDynamicSingleton<KCDatabaseManager> implements Dictionary<DB>, Releasable {
    private HashMap<String, DB> databases;

    @Override
    public void firstBuilt() {
        databases = new HashMap<String, DB>();
    }

    @Override
    public DB get(String name) {
        return databases.get(name);
    }

    private void addDatabase(Document item) {
        String name = Documents.ensureAttributeValue(item, Constants.Attributes.NAME);
        String filepath = StringUtil.evaluateEL(Documents.ensureAttributeValue(item, "file-path"), WikiupNamingDirectory.getInstance());
        File file = new File(filepath);
        if(!file.getParentFile().isDirectory())
            file.getParentFile().mkdirs();
        DB db = new DB();
        databases.put(name, db);
        db.open(filepath, DB.OWRITER | DB.OCREATE);
    }

    @Override
    public void release() {
        for(DB db : databases.values())
            db.close();
        databases.clear();
    }

    public static final class WIRABLE implements Wirable.ByDocument<KCDatabaseManager> {
        @Override
        public KCDatabaseManager wire(Document desc) {
            KCDatabaseManager manager = Wikiup.getModel(KCDatabaseManager.class);
            for(Document item : desc.getChildren())
                manager.addDatabase(item);
            return manager;
        }
    }
}
