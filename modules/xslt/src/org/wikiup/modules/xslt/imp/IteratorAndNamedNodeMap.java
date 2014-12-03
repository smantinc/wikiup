package org.wikiup.modules.xslt.imp;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.wikiup.core.inf.Attribute;

import java.util.Iterator;


public class IteratorAndNamedNodeMap implements NamedNodeMap, Iterable<Attribute> {
    private NamedNodeMap nameNodeMap;
    private Iterable<Attribute> iterable;

    public Node getNamedItem(String name) {
        return nameNodeMap.getNamedItem(name);
    }

    public Node setNamedItem(Node arg) throws DOMException {
        return nameNodeMap.setNamedItem(arg);
    }

    public Node removeNamedItem(String name) throws DOMException {
        return nameNodeMap.removeNamedItem(name);
    }

    public Node item(int index) {
        return nameNodeMap.item(index);
    }

    public int getLength() {
        return nameNodeMap.getLength();
    }

    public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
        return nameNodeMap.getNamedItemNS(namespaceURI, localName);
    }

    public Node setNamedItemNS(Node arg) throws DOMException {
        return nameNodeMap.setNamedItemNS(arg);
    }

    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        return nameNodeMap.removeNamedItemNS(namespaceURI, localName);
    }

    public Iterator<Attribute> iterator() {
        return iterable.iterator();
    }
}
