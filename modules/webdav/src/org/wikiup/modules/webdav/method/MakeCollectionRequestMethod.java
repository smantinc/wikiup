package org.wikiup.modules.webdav.method;

import org.wikiup.core.inf.Document;
import org.wikiup.modules.webdav.imp.WebdavFileSystem;
import org.wikiup.modules.webdav.util.WebdavUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class MakeCollectionRequestMethod implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        String path = WebdavUtil.getWebdavFilePath(context);
        WebdavFileSystem.getInstance().createFileCollection(context, path);
    }
}
