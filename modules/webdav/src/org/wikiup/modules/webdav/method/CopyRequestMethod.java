package org.wikiup.modules.webdav.method;

import org.wikiup.core.inf.Document;
import org.wikiup.modules.webdav.imp.WebdavFileSystem;
import org.wikiup.modules.webdav.util.StatusUtil;
import org.wikiup.modules.webdav.util.WebdavUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class CopyRequestMethod implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        String dest = WebdavUtil.getWebdavFilePath(context.getHeader("Destination"));
        String src = WebdavUtil.getWebdavFilePath(context);
        if(!WebdavFileSystem.getInstance().copy(context, src, dest))
            WebdavUtil.sendError(context, StatusUtil.SC_CONFLICT);
    }
}
