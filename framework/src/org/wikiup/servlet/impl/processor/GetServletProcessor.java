package org.wikiup.servlet.impl.processor;

import org.wikiup.core.util.Assert;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessor;
import org.wikiup.servlet.util.ServletUtil;

import java.io.IOException;

@Deprecated
public class GetServletProcessor implements ServletProcessor {
    public void process(ServletProcessorContext context) {
        try {
            ServletUtil.doGet(context.getServletRequest(), context.getServletResponse());
        } catch(IOException e) {
            Assert.fail(e);
        }
    }
}
