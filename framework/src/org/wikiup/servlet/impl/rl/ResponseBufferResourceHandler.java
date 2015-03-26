package org.wikiup.servlet.impl.rl;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.framework.bootstrap.inf.ResourceHandler;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class ResponseBufferResourceHandler implements ResourceHandler, ServletProcessorContextAware {
    private ServletProcessorContext context;
    private String charset;

    public void handle(Resource resource) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(resource.open(), getCharset());
            StreamUtil.copy(context.getResponseWriter(), reader);
        } catch(UnsupportedEncodingException e) {
            Assert.fail(e);
        } finally {
            StreamUtil.close(reader);
        }
    }

    public void finish() {
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    public String getCharset() {
        return charset != null ? charset : WikiupConfigure.CHAR_SET;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
