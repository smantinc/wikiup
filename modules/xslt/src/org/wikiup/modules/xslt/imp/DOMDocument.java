package org.wikiup.modules.xslt.imp;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;


public class DOMDocument implements Node, Document {
    public String getNodeName() {
        return null;
    }

    public String getNodeValue() throws DOMException {
        return null;
    }

    public void setNodeValue(String nodeValue) throws DOMException {
    }

    public short getNodeType() {
        return 0;
    }

    public NodeList getChildNodes() {
        return null;
    }

    public Node getFirstChild() {
        return null;
    }

    public Node getLastChild() {
        return null;
    }

    public Node getPreviousSibling() {
        return null;
    }

    public Node getNextSibling() {
        return null;
    }

    public IteratorAndNamedNodeMap getAttributes() {
        return null;
    }

    public org.w3c.dom.Document getOwnerDocument() {
        return null;
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        return null;
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        return null;
    }

    public Node removeChild(Node oldChild) throws DOMException {
        return null;
    }

    public Node appendChild(Node newChild) throws DOMException {
        return null;
    }

    public boolean hasChildNodes() {
        return false;
    }

    public Node cloneNode(boolean deep) {
        return null;
    }

    public void normalize() {
    }

    public boolean isSupported(String feature, String version) {
        return false;
    }

    public String getNamespaceURI() {
        return null;
    }

    public String getPrefix() {
        return null;
    }

    public void setPrefix(String prefix) throws DOMException {
    }

    public String getLocalName() {
        return null;
    }

    public boolean hasAttributes() {
        return false;
    }

    public String getBaseURI() {
        return null;
    }

    public short compareDocumentPosition(Node other) throws DOMException {
        return 0;
    }

    public String getTextContent() throws DOMException {
        return null;
    }

    public void setTextContent(String textContent) throws DOMException {
    }

    public boolean isSameNode(Node other) {
        return false;
    }

    public String lookupPrefix(String namespaceURI) {
        return null;
    }

    public boolean isDefaultNamespace(String namespaceURI) {
        return false;
    }

    public String lookupNamespaceURI(String prefix) {
        return null;
    }

    public boolean isEqualNode(Node arg) {
        return false;
    }

    public Object getFeature(String feature, String version) {
        return null;
    }

    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return null;
    }

    public Object getUserData(String key) {
        return null;
    }

    public Document getChild(String name) {
        return null;
    }

    public Document addChild(String name) {
        return null;
    }

    public Iterable<Document> getChildren(String name) {
        return null;
    }

    public Iterable<Document> getChildren() {
        return null;
    }

    public void removeNode(Document child) {
    }

    public DOMDocument getParentNode() {
        return null;
    }

    public Attribute getAttribute(String name) {
        return null;
    }

    public Attribute addAttribute(String name) {
        return null;
    }

    public void removeAttribute(Attribute attr) {
    }

    public String getName() {
        return null;
    }

    public void setName(String name) {
    }

    public Object getObject() {
        return null;
    }

    public void setObject(Object obj) {
    }
}
