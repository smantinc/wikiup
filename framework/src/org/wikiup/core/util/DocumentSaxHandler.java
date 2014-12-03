package org.wikiup.core.util;

import org.wikiup.core.inf.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

public class DocumentSaxHandler extends DefaultHandler {
    private Stack<Document> stack = new Stack<Document>();
    private Document root = null;
    private StringBuilder sb = new StringBuilder();

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        Document doc = stack.pop();
        String text = StringUtil.trim(sb.toString());
        if(text.length() > 0)
            doc.setObject(text);
        sb.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        sb.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        String name = getElementName(qName, localName);
        int i, c = attributes.getLength();
        Document doc = stack.isEmpty() ? root = Documents.create(name) : stack.peek().addChild(name);
        stack.push(doc);

        for(i = 0; i < c; i++)
            Documents.setAttributeValue(doc, getAttributeName(attributes, i), attributes.getValue(i));
    }

    public Document getDocument() {
        return root;
    }

    private String getElementName(String qName, String localName) {
        return StringUtil.isEmpty(qName) ? localName : qName;
    }

    private String getAttributeName(Attributes attrs, int index) {
        String qName = attrs.getQName(index);
        return StringUtil.isEmpty(qName) ? attrs.getLocalName(index) : qName;
    }
}