package org.wikiup.modules.webdav.imp.interceptor;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletInterceptor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class XMLRequestServletInterceptor implements ServletInterceptor {
    public boolean intercept(ServletProcessorContext context) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(context.getServletRequest().getInputStream(), WikiupConfigure.CHAR_SET);
            String text = StreamUtil.loadText(reader);
            if(!StringUtil.isEmpty(text))
                context.setRequstXML(Documents.loadFromString(text));
        } catch(IOException ex) {
        } finally {
            StreamUtil.close(reader);
        }
        return true;
    }
}
