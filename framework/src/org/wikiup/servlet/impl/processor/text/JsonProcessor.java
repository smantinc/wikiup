package org.wikiup.servlet.impl.processor.text;

import org.wikiup.core.impl.document.DocumentArray;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessor;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JsonProcessor implements ServletProcessor {
    public void process(ServletProcessorContext context) {
        StringWriter writer = context.getResponseBuffer().getResponseWriter();
        String callback = context.getParameter("callback", null);
        Document doc = context.getResponseXML();
        if(callback != null)
            writer.write(callback);
        writer.write('(');
        writeDocument(writer, doc);
        writer.write(')');
        if(callback != null)
            writer.write(";");
    }

    private void writeDocument(StringWriter writer, Document doc) {
        Iterator<Document> iterator = doc.getChildren().iterator();
        if(iterator.hasNext()) {
            if(!doc.getAttributes().iterator().hasNext())
                writeArray(writer, iterator, !(doc instanceof DocumentArray));
            else {
                Set<String> names = new HashSet<String>();
                writer.write('{');
                writeObject(writer, doc, false);
                while(iterator.hasNext())
                    names.add(iterator.next().getName());
                for(String name : names) {
                    writer.write(",\"" + name + "\":");
                    writeArray(writer, doc.getChildren(name).iterator(), true);
                }
                writer.write('}');
            }
        } else
            writeObject(writer, doc, true);
    }

    private void writeArray(StringWriter writer, Iterator<Document> iterator, boolean brackets) {
        if(brackets)
            writer.write('[');
        while(iterator.hasNext()) {
            writeDocument(writer, iterator.next());
            if(iterator.hasNext())
                writer.write(',');
        }
        if(brackets)
            writer.write(']');
    }

    private void writeObject(StringWriter writer, Document node, boolean warp) {
        StringBuilder buf = new StringBuilder();
        for(Attribute attr : node.getAttributes()) {
            boolean quote = needQuote(attr.getObject()) && attr.toString() != null;
            buf.append('"');
            buf.append(attr.getName());
            buf.append("\":");
            if(quote) {
                buf.append('"');
                buf.append(attr.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n"));
                buf.append('"');
            } else
                buf.append(attr.getObject() != null ? attr.toString() : "null");
            buf.append(',');
        }
        if(buf.length() > 0)
            buf.setLength(buf.length() - 1);
        if(warp)
            writer.write('{');
        writer.write(buf.toString());
        if(warp)
            writer.write('}');
    }

    private boolean needQuote(Object object) {
        if(object == null || object instanceof Number || object instanceof Boolean)
            return false;
        return true;
    }
}
