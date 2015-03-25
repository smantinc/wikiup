package org.wikiup.servlet.impl.processor.text;

import org.wikiup.framework.bean.WikiupConfigure;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SsiServletProcessor implements ServletProcessor {
    private static Pattern SSIPattern = Pattern.compile("(?:<%!|<!\\-\\-#)include\\s+(file|uri|virtual)=\"([^\"]*)\"\\s*(?:%|\\-\\-)>");

    public void process(ServletProcessorContext context) {
        StringWriter writer = context.getResponseWriter();
        String text = context.getResponseText();
        doProcess(context, writer, text);
    }

    private void doProcess(ServletProcessorContext context, StringWriter writer, String text) {
        Matcher matcher = SSIPattern.matcher(text);
        int offset = 0;
        while(matcher.find()) {
            boolean virtual = StringUtil.compareIgnoreCase("virtual", matcher.group(1));
            String path = matcher.group(2);
            writer.write(text, offset, matcher.start() - offset);
            if(virtual) {
                ServletProcessorContext c = new ServletProcessorContext(context, getContextURI(context, path));
                c.doInit();
                c.doProcess();
                c.release();
                writer.write(c.getResponseBuffer().getResponseText(false));
            } else {
                InputStreamReader reader = null;
                try {
                    Resource resource = context.getResource(getContextURI(context, path));
                    Assert.isTrue(resource.exists(), resource.getURI());
                    reader = new InputStreamReader(resource.open(), WikiupConfigure.CHAR_SET);
                    StreamUtil.copy(writer, reader);
                } catch(IOException ex) {
                    Assert.fail(ex);
                } finally {
                    StreamUtil.close(reader);
                }
            }
            offset = matcher.end();
        }
        writer.write(text, offset, text.length() - offset);
    }

    private String getContextURI(ServletProcessorContext context, String uri) {
        return context.getContextPath(uri.charAt(0) == '/' ? uri : FileUtil.joinPath(FileUtil.getFilePath(context.getRequestURI(), '/'), uri, '/'));
    }
}
