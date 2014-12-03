package org.wikiup.modules.webdav.util;

import org.wikiup.core.util.Assert;
import org.wikiup.core.util.StringUtil;
import org.wikiup.modules.webdav.imp.WebdavFileSystem;
import org.wikiup.modules.webdav.inf.FileInf;
import org.wikiup.servlet.ServletProcessorContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebdavUtil {
    static public FileInf getWebdavFile(ServletProcessorContext context, String url) {
        return WebdavFileSystem.getInstance().getFile(context, url);
    }

    static public FileInf getWebdavFile(ServletProcessorContext context) {
        return getWebdavFile(context, getWebdavFilePath(context));
    }

    public static String getWebdavFilePath(String url) {
        try {
            return url != null ? (new URI(url)).getPath() : null;
        } catch(URISyntaxException ex) {
            Assert.fail(ex);
        }
        return null;
    }

    public static String getWebdavFilePath(ServletProcessorContext context) {
        return getWebdavFilePath(context.getRequestURI());
    }

    public static void sendError(ServletProcessorContext context, int sc) {
        try {
            context.getServletResponse().sendError(sc, StatusUtil.getStatusText(sc));
        } catch(IOException ex) {
            Assert.fail(ex);
        }
    }

    static public String generateLockToken() {
        StringBuffer buffer = new StringBuffer();
        long seed = System.currentTimeMillis();
        buffer.append("opaquelocktoken:");
        buffer.append(StringUtil.generateRandomString(8, seed).toLowerCase());
        buffer.append('-');
        buffer.append(StringUtil.generateRandomString(4, seed + 1).toLowerCase());
        buffer.append('-');
        buffer.append(StringUtil.generateRandomString(4, seed + 2).toLowerCase());
        buffer.append('-');
        buffer.append(StringUtil.generateRandomString(4, seed + 3).toLowerCase());
        buffer.append('-');
        buffer.append(StringUtil.generateRandomString(12, seed + 4).toLowerCase());
        return buffer.toString();
    }
}
