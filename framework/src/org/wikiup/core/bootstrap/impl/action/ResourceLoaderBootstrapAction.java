package org.wikiup.core.bootstrap.impl.action;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupResourceLoader;
import org.wikiup.core.bootstrap.Bootstrap;
import org.wikiup.core.bootstrap.BootstrapResource;
import org.wikiup.core.bootstrap.inf.ext.BootstrapAction;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Documents;

public class ResourceLoaderBootstrapAction implements BootstrapAction {

    public void doAction(Bootstrap bootstrap, Document desc) {
        WikiupResourceLoader loader = Wikiup.getModel(WikiupResourceLoader.class);
        for(Document node : desc.getChildren()) {
            BootstrapResource br = bootstrap.getBootstrapResource();
            for(Resource res : loader.get(Documents.getDocumentValue(node)))
                br.append(res, true);
        }
    }
}
