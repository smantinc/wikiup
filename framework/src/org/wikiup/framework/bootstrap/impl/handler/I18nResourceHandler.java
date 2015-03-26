package org.wikiup.framework.bootstrap.impl.handler;

import org.wikiup.core.bean.I18nResourceManager;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.Resource;

public class I18nResourceHandler extends DirectoryDocumentResourceHandler {
    @Override
    protected void loadDirectoryResource(Resource resource, Document doc, String[] path) {
        I18nResourceManager.getInstance().mergeConfigure(path, doc);
    }
}
