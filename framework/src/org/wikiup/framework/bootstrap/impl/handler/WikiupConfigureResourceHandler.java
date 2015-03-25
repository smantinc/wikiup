package org.wikiup.framework.bootstrap.impl.handler;

import org.wikiup.framework.bean.WikiupConfigure;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.Resource;

public class WikiupConfigureResourceHandler extends DirectoryDocumentResourceHandler {
    @Override
    protected void loadDirectoryResource(Resource resource, Document doc, String[] path) {
        WikiupConfigure.getInstance().mergeConfigure(path, doc);
    }
}
