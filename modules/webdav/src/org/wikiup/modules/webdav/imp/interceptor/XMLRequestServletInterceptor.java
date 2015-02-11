package org.wikiup.modules.webdav.imp.interceptor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.modules.webdav.util.XMLRequest;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletInterceptor;

public class XMLRequestServletInterceptor implements ServletInterceptor {
    public boolean intercept(ServletProcessorContext context) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(context.getServletRequest().getInputStream(), WikiupConfigure.CHAR_SET);
            String text = StreamUtil.loadText(reader);
            XMLRequest xmlRequest = context.query(XMLRequest.class);
            if(!StringUtil.isEmpty(text))
                xmlRequest.setXMLRequest(Documents.loadFromString(text));
        } catch(IOException ex) {
        } finally {
            StreamUtil.close(reader);
        }
        return true;
    }
}
