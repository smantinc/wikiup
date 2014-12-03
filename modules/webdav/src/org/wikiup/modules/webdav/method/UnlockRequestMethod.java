package org.wikiup.modules.webdav.method;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.StringUtil;
import org.wikiup.modules.webdav.imp.WebdavFileSystem;
import org.wikiup.modules.webdav.util.StatusUtil;
import org.wikiup.modules.webdav.util.WebdavUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class UnlockRequestMethod implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        String src = WebdavUtil.getWebdavFilePath(context);
        String token = context.getHeader("Lock-Token");
        if(token != null)
            token = StringUtil.trim(token, "(<>)");
        if(!WebdavFileSystem.getInstance().unlock(context, src, token))
            WebdavUtil.sendError(context, StatusUtil.SC_LOCKED);
    }
}
