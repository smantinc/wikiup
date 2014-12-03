package org.wikiup.modules.webdav.method;

import org.wikiup.core.impl.document.DocumentImpl;
import org.wikiup.core.impl.document.DocumentWithNamespace;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.modules.webdav.imp.WebdavFileSystem;
import org.wikiup.modules.webdav.util.StatusUtil;
import org.wikiup.modules.webdav.util.WebdavUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class LockRequestMethod implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        String src = WebdavUtil.getWebdavFilePath(context);
        Document response = new DocumentWithNamespace(new DocumentImpl("prop"), "D:", "DAV:");
        String token = context.getHeader("If");
        if(token != null)
            token = StringUtil.trim(token, "(<>)");
        if(!WebdavFileSystem.getInstance().lock(context, src, token, context.getRequstXML(), response))
            WebdavUtil.sendError(context, StatusUtil.SC_LOCKED);
        else
            Documents.merge(context.getResponseBuffer().getResponseXML("D:prop", true), response);
    }
}
