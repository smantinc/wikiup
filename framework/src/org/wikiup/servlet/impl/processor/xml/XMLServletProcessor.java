package org.wikiup.servlet.impl.processor.xml;

import org.wikiup.framework.bean.WikiupConfigure;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Documents;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessor;

import java.io.Writer;

public class XMLServletProcessor implements ServletProcessor, DocumentAware {
    private boolean encode = true;
    private boolean autoCreate = true;

    public void process(ServletProcessorContext context) {
        Document response = context.getResponseBuffer().getResponseXML("root", autoCreate);
        if(response != null) {
            Writer writer = context.getResponseWriter();
            Documents.writeXMLHeader(writer, WikiupConfigure.CHAR_SET);
            Documents.printToWriter(response, writer, 0, encode);
        }
    }

    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    public boolean isEncode() {
        return encode;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    public void aware(Document desc) {
        setEncode(Documents.getAttributeBooleanValue(desc, "encode", true));
        setAutoCreate(Documents.getAttributeBooleanValue(desc, "auto-create", true));
    }
}
