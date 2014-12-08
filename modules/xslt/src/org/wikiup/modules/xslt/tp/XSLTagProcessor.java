package org.wikiup.modules.xslt.tp;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.modules.xslt.TransformerCache;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.TagProcessor;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class XSLTagProcessor implements TagProcessor {
    public void process(ServletProcessorContext context, TagProcessor parent, String body, Getter<?> parameters, StringWriter writer) {
        try {
            StringWriter buf = new StringWriter();
            boolean eval = ValueUtil.toBoolean(parameters.get("evaluate-el"), false);
            Transformer transformer = TransformerCache.getInstance().getTransformer(context.getRealPathByURI(parameters.get("href").toString()));
            Documents.writeXMLHeader(buf, WikiupConfigure.CHAR_SET);
            buf.write("<root>");
            parent.process(context, parent, body, parameters, buf);
            buf.write("</root>");
            transformer.transform(new StreamSource(new StringReader(replaceHTMLEntities(buf.toString()))), new StreamResult(new XSLTOutputWriter(context, writer, eval)));
        } catch(TransformerException ex) {
            Assert.fail(ex);
        }
    }

    private String replaceHTMLEntities(String html) {
        StringBuilder buffer = new StringBuilder(html);
        replaceHTMLEntity(buffer, 1, "<?xml ", "?>", "");
        replaceHTMLEntity(buffer, 0, "&nbsp", ";", "&#160;");
        return buffer.toString();
    }

    private void replaceHTMLEntity(StringBuilder buffer, int offset, String start, String end, String replacement) {
        int idx = offset;
        while(idx != -1)
            idx = replaceStringBuffer(buffer, start, end, idx, replacement);
    }

    private int replaceStringBuffer(StringBuilder buffer, String start, String end, int offset, String replacement) {
        int idx = buffer.indexOf(start, offset);
        if(idx != -1) {
            int e = buffer.indexOf(end, idx);
            int l = end.length();
            if(e != -1)
                buffer.replace(idx, e + l, replacement);
            return e != -1 ? e - replacement.length() : -1;
        }
        return -1;
    }

    static public class XSLTOutputWriter extends Writer {
        private Writer writer;
        private StringBuilder buffer = new StringBuilder();
        private ServletProcessorContext context;
        private boolean eval;

        public XSLTOutputWriter(ServletProcessorContext context, Writer writer, boolean eval) {
            this.context = context;
            this.writer = writer;
            this.eval = eval;
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            buffer.append(cbuf, off, len);
        }

        public void flush() throws IOException {
            writer.write(eval ? StringUtil.evaluateEL(buffer.toString(), context) : buffer.toString());
            writer.flush();
            buffer.setLength(0);
        }

        public void close() throws IOException {
            writer.close();
        }
    }
}
