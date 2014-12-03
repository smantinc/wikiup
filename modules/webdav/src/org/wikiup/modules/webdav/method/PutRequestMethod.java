package org.wikiup.modules.webdav.method;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.modules.webdav.imp.WebdavFileSystem;
import org.wikiup.modules.webdav.inf.FileInf;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class PutRequestMethod implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        try {
            URI uri = new URI(context.getRequestURI());
            FileInf file = WebdavFileSystem.getInstance().getFile(context, uri.getPath());
            OutputStream output = file.getOutputStream();
            try {
                StreamUtil.copy(output, context.getServletRequest().getInputStream());
            } finally {
                StreamUtil.close(output);
            }
        } catch(URISyntaxException ex) {
            Assert.fail(ex);
        } catch(IOException ex) {
            Assert.fail(ex);
        }
    }
}
