package org.wikiup.servlet.beans;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Documents;

import java.util.HashMap;
import java.util.Map;

public class MimeTypes extends WikiupDynamicSingleton<MimeTypes> implements Dictionary<String>, DocumentAware {
    private Map<String, String> contentTypes;

    public void firstBuilt() {
        contentTypes = new HashMap<String, String>();
    }

    public String get(String name) {
        return contentTypes.get(name);
    }

    @Override
    public void aware(Document desc) {
        Document doc = WikiupConfigure.getInstance().lookup("wk/mime-mappings");
        if(doc != null)
            for(Document node : doc.getChildren("mime-mapping"))
                contentTypes.put(Documents.getChildValue(node, "extension"), Documents.getChildValue(node, "mime-type"));
    }
}