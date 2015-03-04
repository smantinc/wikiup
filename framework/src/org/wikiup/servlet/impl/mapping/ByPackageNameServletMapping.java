package org.wikiup.servlet.impl.mapping;

import java.util.Collections;
import java.util.LinkedList;

import org.wikiup.core.Constants;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletConfigureMapping;

public class ByPackageNameServletMapping implements ServletConfigureMapping, DocumentAware {
    private static final String[] SCAN_NODE_NAMES = {"Action", "Context"};
    private Document configure;

    public Document map(ServletProcessorContext context) {
        String serverName = context.getContextAttribute(configure, "host-name");
        String path = context.getContextAttribute(configure, "package-name", "");
        LinkedList<String> pkgs = new LinkedList<String>();
        for(String name : serverName.split("\\."))
            pkgs.add(0, name);
        Collections.addAll(pkgs, path.split("/"));
        Document doc = Documents.create(Constants.Elements.ROOT);
        String className = StringUtil.connect(StringUtil.join(pkgs, "."), FileUtil.getFileName(context.getContextAttribute(configure, "class-name"), '/'), '.');
        boolean found = false;
        for(String nodeName : SCAN_NODE_NAMES)
            found = scanClassComponent(doc, nodeName, className) || found;
        return found ? doc : null;
    }

    private boolean scanClassComponent(Document doc, String nodeName, String className) {
        Class<?> clazz = Interfaces.tryGetClass(className + nodeName);
        if(clazz != null) {
            Document node = doc.addChild(nodeName.toLowerCase());
            Documents.setAttributeValue(node, Constants.Elements.CLASS, clazz.getName());
        }
        return clazz != null;
    }

    public void appendEntry(String uriPattern, ServletMappingEntry node) {
    }

    public Boolean translate(String object) {
        return true;
    }

    public void aware(Document desc) {
        configure = desc;
    }
}
