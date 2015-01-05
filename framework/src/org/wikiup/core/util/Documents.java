package org.wikiup.core.util;

import org.wikiup.core.impl.df.DirectoryDocumentReader;
import org.wikiup.core.impl.df.FileDocumentReader;
import org.wikiup.core.impl.df.StringDocumentReader;
import org.wikiup.core.impl.df.XmlStreamDocumentReader;
import org.wikiup.core.impl.df.YamlStreamDocumentReader;
import org.wikiup.core.impl.document.DocumentImpl;
import org.wikiup.core.impl.document.DocumentWrapper;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Element;
import org.wikiup.core.inf.ExceptionHandler;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.inf.ext.DocumentReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;

public class Documents {
    private static final String S_TOKEN = "<";
    private static final String E_TOKEN = ">";
    private static final String T_TOKEN = "/>";
    private static final String LB = "\r\n";

    static public DocumentImpl create(String name) {
        return new DocumentImpl(name);
    }

    static public Document clone(Document doc) {
        Document c = create(doc.getName());
        merge(c, doc);
        return c;
    }

    static public String getId(Document doc, String def) {
        String name = getAttributeValue(doc, "name", null);
        return name != null ? name : getAttributeValue(doc, "id", def);
    }

    static public String getId(Document doc) {
        return getId(doc, null);
    }

    static public String getDocumentValue(Document doc) {
        String value = ValueUtil.toString(doc.getAttribute("value"));
        return value != null ? value : ValueUtil.toString(doc);
    }

    static public String getDocumentValue(Document doc, String name) {
        return getDocumentValue(doc, name, null);
    }

    static public String getDocumentValue(Document doc, String name, String defValue) {
        String value = getAttributeValue(doc, name, null);
        return value != null ? value : getChildValue(doc, name, defValue);
    }

    static public String getDocumentValueByXPath(Document doc, String path) {
        return getDocumentValueByXPath(doc, path, "");
    }

    static public String getDocumentValueByXPath(Document doc, String path, String defValue) {
        try {
            Attribute vs = getAttributeByXPath(doc, path);
            return vs != null ? ValueUtil.toString(vs) : defValue;
        } catch(Exception ex) {
            return defValue;
        }
    }

    static public int getDocumentInteger(Document doc, String name, int defValue) {
        return ValueUtil.toInteger(getDocumentValue(doc, name, null), defValue);
    }

    static public Attribute touchAttribute(Element node, String name) {
        Attribute a = node.getAttribute(name);
        return a != null ? a : node.addAttribute(name);
    }

    static public Document touchChild(Document node, String name) {
        Document t = node.getChild(name);
        return t != null ? t : node.addChild(name);
    }

    static public Document touchDocument(Document doc, String[] paths, int depth) {
        int i;
        for(i = 0; i < depth; i++)
            if(!StringUtil.isEmpty(paths[i]))
                doc = touchChild(doc, paths[i]);
        return doc;
    }

    static public Document touchDocument(Document doc, String path) {
        String paths[] = path.split("/");
        return touchDocument(doc, paths, paths.length - 1);
    }

    static public Attribute setAttributeValue(Element node, String name, Object value) {
        Attribute vs = touchAttribute(node, name);
        vs.setObject(value);
        return vs;
    }

    static public Document setChildValue(Document node, String name, String value) {
        Document doc = touchChild(node, name);
        doc.setObject(value);
        return doc;
    }

    static public String getAttributeValue(Element node, String name, String defValue) {
        String value = ValueUtil.toString(node.getAttribute(name));
        return value != null ? value : defValue;
    }

    static public String getAttributeValue(Element node, String name) {
        return getAttributeValue(node, name, "");
    }

    static public boolean getAttributeBooleanValue(Element node, String name, boolean defValue) {
        return ValueUtil.toBoolean(getAttributeValue(node, name, null), defValue);
    }

    static public int getAttributeIntegerValue(Element node, String name, int defValue) {
        return ValueUtil.toInteger(getAttributeValue(node, name, null), defValue);
    }

    static public String getChildValue(Document doc, String name) {
        return getChildValue(doc, name, "");
    }

    static public String getChildValue(Document doc, String name, String def) {
        Document child = doc.getChild(name);
        String value = ValueUtil.toString(child);
        return value != null ? value : (child != null ? "" : def);
    }

    static public Attribute getAttributeByXPath(Document doc, String xpath) {
        return new XPath(xpath).getAttribute(doc);
    }

    static public Document getDocumentByXPath(Document doc, String xpath) {
        return (Document) new XPath(xpath).getAttribute(doc);
    }

    static public Document findMatchesChild(Document doc, String attribute, String value) {
        return findMatchesChild(doc.getChildren(), attribute, value);
    }

    static public Document findMatchesChild(Document doc, String childName, String attribute, String value) {
        return findMatchesChild(doc.getChildren(childName), attribute, value);
    }

    static private Document findMatchesChild(Iterable<Document> iterable, String attribute, String value) {
        for(Document node : iterable)
            if(value.matches(getAttributeValue(node, attribute)))
                return node;
        return null;
    }

    static public Document loadFromURL(String url) {
        try {
            return loadFromURL(new URL(url));
        } catch(IOException ex) {
            return null;
        }
    }

    static public Document loadFromURL(URL url) {
        try {
            return loadXmlFromStream(url.openStream(), true);
        } catch(IOException ex) {
            Assert.fail(ex);
        }
        return null;
    }

    static private DocumentReader<InputStream> getDocumentReaderByExt(String ext) {
        if("xml".equalsIgnoreCase(ext))
            return new XmlStreamDocumentReader();
        if("yaml".equalsIgnoreCase(ext))
            return new YamlStreamDocumentReader();
        return null;
    }

    static public Document loadXmlFromStream(InputStream input, boolean close) {
        try {
            return new XmlStreamDocumentReader().translate(input);
        } finally {
            if(close)
                StreamUtil.close(input);
        }
    }

    static public Document loadYamlFromStream(InputStream input, boolean close) {
        Document xml;
        try {
            xml = new YamlStreamDocumentReader().translate(input);
        } finally {
            if(close)
                StreamUtil.close(input);
        }
        return xml;
    }

    static public Document loadFromStream(InputStream input) {
        return loadXmlFromStream(input, false);
    }

    static public Document loadFromString(String str) {
        return new StringDocumentReader().translate(str);
    }

    static public Document loadFromResource(Resource resource) {
        String ext = FileUtil.getFileExt(resource.getURI());
        DocumentReader<InputStream> reader = getDocumentReaderByExt(ext);
        Assert.notNull(reader, ext);
        InputStream stream = resource.open();
        try {
            return reader.translate(stream);
        } finally {
            StreamUtil.close(stream);
        }
    }

    static public Document loadFromFile(String fileName) {
        return loadFromFile(FileUtil.getFile(fileName));
    }

    static public Document loadFromFile(File file) {
        return new FileDocumentReader().translate(file);
    }

    static public Document loadFromDirectory(String dirName) {
        return new DirectoryDocumentReader().translate(FileUtil.getFile(dirName));
    }

    static public void clearDocument(Document doc) {
        clearAttribute(doc);
        clearChild(doc);
    }

    static public void clearChild(Document doc) {
        Iterator iterator = doc.getChildren().iterator();
        while(iterator.hasNext())
            iterator.remove();
    }

    static public void clearAttribute(Element node) {
        Iterator iterator = node.getAttributes().iterator();
        while(iterator.hasNext())
            iterator.remove();
    }

    static public Document merge(Document target, Document source) {
        target.setObject(source.getObject());
        mergeAttribute(target, source);
        for(Document doc : source.getChildren())
            merge(target.addChild(doc.getName()), doc);
        return target;
    }

    static public Document mergeChildren(Document target, Iterable<Document> iterable, boolean cast) {
        return mergeChildren(target, iterable, null, cast);
    }

    static public Document mergeChildren(Document target, Iterable<Document> iterable, String nodeName, boolean cast) {
        DocumentImpl t = cast ? Interfaces.cast(DocumentImpl.class, target) : null;
        for(Document doc : iterable)
            if(t != null)
                if(nodeName == null)
                    t.addChild(doc.getName(), doc);
                else
                    t.addChild(nodeName, new NamedDocument(nodeName, doc));
            else
                merge(target.addChild(nodeName != null ? nodeName : doc.getName()), doc);
        return target;
    }

    public static String ensureAttributeValue(Document desc, String name) {
        String value = getAttributeValue(desc, name, null);
        Assert.notNull(value, IllegalArgumentException.class);
        return value;
    }

    private static class NamedDocument extends DocumentWrapper {
        private String name;

        public NamedDocument(String name, Document doc) {
            super(doc);
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    static public void mergeAttribute(Document target, Document source) {
        for(Attribute attribute : source.getAttributes())
            touchAttribute(target, attribute.getName()).setObject(attribute.getObject());
    }

    static public void copy(Document target, Document source) {
        clearDocument(target);
        merge(target, source);
    }

    static public void printToWriter(Document doc, Writer writer) {
        printToWriter(doc, writer, 0, true);
    }

    static public void printToWriter(Document doc, Writer writer, int indent) {
        printToWriter(doc, writer, indent, true);
    }

    static public void printToWriter(Document doc, Writer writer, int indent, boolean encode) {
        String data, name = doc.getName() != null ? doc.getName() : "";
        Iterator<Document> iterator = doc.getChildren().iterator();
        boolean isLeave = !iterator.hasNext(), hasData;
        try {
            printIndent(writer, indent);
            writer.write(S_TOKEN);
            writer.write(name);
            printAttributes(writer, doc);
            data = encode ? encode(ValueUtil.toString(doc)) : ValueUtil.toString(doc);
            hasData = data != null;
            if(isLeave && !hasData) {
                writer.write(T_TOKEN);
                writer.write(LB);
            } else {
                writer.write(E_TOKEN);
                if(hasData && data.length() > 0) {
                    if(isLeave)
                        writer.write(data);
                    else {
                        writer.write(LB);
                        printIndent(writer, indent + 1);
                        writer.write(data);
                        writer.write(LB);
                    }
                }
                if(!isLeave) {
                    writer.write(LB);
                    while(iterator.hasNext())
                        printToWriter(iterator.next(), writer, indent + 1, encode);
                }
                if(!isLeave || !hasData)
                    printIndent(writer, indent);
                writer.write(S_TOKEN + "/" + name + E_TOKEN + LB);
            }
        } catch(IOException ex) {
            Assert.fail(ex);
        }
    }

    public static void save(Document doc, File file, String charset) {
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), charset);
            writeXMLHeader(writer, charset);
            Documents.printToWriter(doc, writer);
            StreamUtil.close(writer);
        } catch(FileNotFoundException e) {
            Assert.fail(e);
        } catch(UnsupportedEncodingException e) {
            Assert.fail(e);
        } finally {
            StreamUtil.close(writer);
        }
    }

    public static String getNamespace(Document doc, String name) {
        for(Attribute attr : doc.getAttributes()) {
            String attrName = attr.getName();
            if(attrName.startsWith("xmlns") && attr.getObject().equals(name))
                return attrName.length() > 6 ? attrName.substring(6) : "";
        }
        return name;
    }

    private static void printAttributes(Writer writer, Document node) throws IOException {
        for(Attribute attr : node.getAttributes()) {
            String value = ValueUtil.toString(attr);
            writer.write(" " + attr.getName() + "=\"" + (value == null ? "" : encode(value)) + "\"");
        }
    }

    public static void printIndent(Writer writer, int indent) throws IOException {
        int i;
        for(i = 0; i < indent; i++)
            writer.write("  ");
    }

    public static String encode(String xml) {
        return xml != null ? xml.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;") : xml;
    }

    public static Document getDocumentByPath(Document node, String route[], int length) {
        return getDocumentByPath(node, route, length, false);
    }

    public static Document getDocumentByPath(Document node, String route[], int length, boolean create) {
        int i;
        for(i = 0; i < length && route != null; i++)
            if(!StringUtil.isEmpty(route[i])) {
                node = create ? touchChild(node, route[i]) : node.getChild(route[i]);
                if(node == null)
                    break;
            }
        return node;
    }

    public static void writeXMLHeader(Writer writer, String cs) {
        writeXMLHeader(writer, cs, null);
    }

    public static void writeXMLHeader(Writer writer, String cs, ExceptionHandler eh) {
        try {
            writer.write("<?xml version=\"1.0\" encoding=\"" + cs + "\"?>\n");
        } catch(IOException e) {
            if(!Interfaces.handleException(eh, e))
                Assert.fail(e);
        }
    }
}
