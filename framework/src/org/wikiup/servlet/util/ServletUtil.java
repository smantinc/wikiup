package org.wikiup.servlet.util;

import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.beans.MimeTypes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ServletUtil {
    static private String[] WNDI_PROCESSOR_TEMPLATE = {"wk", "servlet", "processor", "by-extension"};

    static public Getter<?> getServletProcessorByExtension() {
        return Wikiup.getInstance().get(Getter.class, WNDI_PROCESSOR_TEMPLATE);
    }

    static public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = getResourcePath(request);
        File file = new File(request.getSession().getServletContext().getRealPath(path));
        if(file.exists()) {
            OutputStream os = response.getOutputStream();
            InputStream is = null;
            try {
                String contentType = Wikiup.getModel(MimeTypes.class).get(FileUtil.getFileExt(path));
                if(contentType != null)
                    response.setContentType(contentType);
                response.setContentLength((int) file.length());

                is = new FileInputStream(file);
                StreamUtil.copy(os, is);
            } finally {
                if(is != null)
                    is.close();
                os.flush();
                os.close();
            }
        } else
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    static public String getResourcePath(HttpServletRequest request) {
        String contextPath = StringUtil.shrinkLeft(request.getContextPath(), "/");
        String u = StringUtil.shrinkLeft(request.getRequestURI(), "/");
        String url = StringUtil.shrinkLeft(u, contextPath);
        return "/" + FileUtil.decodeFileName(url);
    }
}
