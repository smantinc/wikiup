package org.wikiup.modules.webdav.method;

import org.wikiup.core.inf.Document;
import org.wikiup.modules.webdav.imp.WebdavFileSystem;
import org.wikiup.modules.webdav.inf.FileInf;
import org.wikiup.modules.webdav.util.WebdavUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

public class HeadRequestMethod implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        try {
            URI uri = new URI(context.getRequestURI());
            FileInf file = WebdavFileSystem.getInstance().getFile(context, uri.getPath());
            if(!file.exists())
                WebdavUtil.sendError(context, HttpServletResponse.SC_NOT_FOUND);
        } catch(URISyntaxException ex) {
            WebdavUtil.sendError(context, HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
