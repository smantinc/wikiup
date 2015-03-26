package org.wikiup.framework.bootstrap.impl.action;

import org.wikiup.Wikiup;
import org.wikiup.framework.bean.WikiupResourceLoader;
import org.wikiup.framework.bootstrap.Bootstrap;
import org.wikiup.framework.bootstrap.BootstrapResource;
import org.wikiup.framework.bootstrap.inf.ext.BootstrapAction;
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
