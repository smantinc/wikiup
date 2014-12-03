package org.wikiup.modules.webdav.method;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.modules.webdav.inf.FileInf;
import org.wikiup.modules.webdav.util.WebdavUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

import java.io.InputStream;

public class GetRequestMethod implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        FileInf file = WebdavUtil.getWebdavFile(context);
        InputStream is = file.getInputStream();
        context.setHeader("Allow", "GET, HEAD, PUT, LOCK, UNLOCK");
        context.setContentType(file.getContentType());
        StreamUtil.copy(context.getResponseOutputStream(), is);
        StreamUtil.close(is);
    }
}
