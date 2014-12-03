package org.wikiup.modules.webdav.method;

import org.wikiup.core.inf.Document;
import org.wikiup.modules.webdav.imp.WebdavFileSystem;
import org.wikiup.modules.webdav.util.StatusUtil;
import org.wikiup.modules.webdav.util.WebdavUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class DeleteRequestMethod implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        String src = WebdavUtil.getWebdavFilePath(context);
        if(!WebdavFileSystem.getInstance().getFileSystem(context, src).delete(src))
            WebdavUtil.sendError(context, StatusUtil.SC_LOCKED);
    }
}
