package org.wikiup.modules.velocity;

import org.apache.velocity.app.VelocityEngine;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;

import java.util.ArrayList;
import java.util.List;

public class WikiupVelocityEngine extends WikiupDynamicSingleton<WikiupVelocityEngine> implements DocumentAware {
    private VelocityEngine engine = null;
    private List<String> fileResourceLoadPaths;

    public static WikiupVelocityEngine getInstance() {
        return getInstance(WikiupVelocityEngine.class);
    }

    public VelocityEngine getEngine() {
        if(engine == null) {
            engine = new VelocityEngine();
            try {
                engine.init();
            } catch(Exception ex) {
                Assert.fail(ex);
            }
        }
        return engine;
    }

//  public void cloneFrom(WikiupVelocityEngine instance)
//  {
//    fileResourceLoadPaths = instance.fileResourceLoadPaths;
//  }

    public void firstBuilt() {
        fileResourceLoadPaths = new ArrayList<String>();
    }

    @Override
    public void aware(Document desc) {
        for(Document doc : desc.getChildren())
            fileResourceLoadPaths.add(Documents.getDocumentValue(doc));
    }
}
