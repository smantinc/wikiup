package org.wikiup.servlet.impl.processor.binary;

import org.wikiup.core.util.Assert;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.ServletProcessorResponseBuffer;
import org.wikiup.servlet.inf.ServletProcessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GZipCompressServletProcessor implements ServletProcessor {
    public void process(ServletProcessorContext context) {
        ServletProcessorResponseBuffer responseBuf = context.getResponseBuffer();
        byte[] bytes = responseBuf.getResponseBytes(false);
        String text = responseBuf.getResponseText(false);
        byte[] b = text != null ? text.getBytes() : bytes;
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        ByteArrayOutputStream os = responseBuf.getResponseStream();
        responseBuf.release();
        try {
            GZIPOutputStream gzo = new GZIPOutputStream(os);
            StreamUtil.copy(gzo, is);
            is.close();
            gzo.close();
        } catch(IOException ex) {
            Assert.fail(ex);
        }
        context.setHeader("Content-Encoding", "gzip");
        context.setHeader("Content-Length", String.valueOf(os.size()));
    }
}
