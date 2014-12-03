package org.wikiup.core.util;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XPath {
    private String path;
    private String condition;
    private String attribute;

    final static private Pattern XPATH_PATTERN = Pattern.compile("([/\\-\\w]*)(?:\\[@?([^\\]]+)\\])?(?:\\.(.+))?");

    public XPath(String xpath) {
        Matcher matcher = XPATH_PATTERN.matcher(xpath);
        if(matcher.matches()) {
            path = matcher.group(1);
            condition = matcher.group(2);
            attribute = matcher.group(3);
        }
    }

    public boolean isXPath() {
        return path != null;
    }

    public Attribute getAttribute(Document target) {
        Document node = getDocument(target);
        if(node != null && condition != null) {
            if(condition.matches("[\\w\\d\\.\\-_]+"))
                return node.getAttribute(condition);
            else if(condition.matches("[\\w\\d\\.\\-_]+=.+"))
                return searchDocument(getParentDocument(target), node.getName(), condition);
        }
        return node;
    }

    public Document searchDocument(Document parent, String nodeName, String condition) {
        int pos = condition.indexOf('=');
        String name = condition.substring(0, pos);
        String value = StringUtil.trim(condition.substring(pos + 1), "'\"");
        for(Document node : parent.getChildren(nodeName))
            if(StringUtil.compare(Documents.getAttributeValue(node, name, null), value))
                return node;
        return null;
    }

    public Document getDocument(Document target) {
        if(path != null) {
            String route[] = path.split("/");
            return Documents.getDocumentByPath(target, route, route.length);
        }
        return null;
    }

    public Document getParentDocument(Document target) {
        if(path != null) {
            String route[] = path.split("/");
            return Documents.getDocumentByPath(target, route, route.length - 1);
        }
        return null;
    }

    public String getPath() {
        return path;
    }

    public String getCondition() {
        return condition;
    }

    public String getAttribute() {
        return attribute;
    }
}
