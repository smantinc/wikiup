package org.wikiup.modules.webdav.method;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.modules.webdav.imp.FileDOM;
import org.wikiup.modules.webdav.imp.WebdavFileSystem;
import org.wikiup.modules.webdav.inf.FileInf;
import org.wikiup.modules.webdav.util.StatusUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

import java.net.URI;
import java.net.URISyntaxException;

public class PropFindRequestMethod implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        int depth = ValueUtil.toInteger(context.getServletRequest().getHeader("Depth"), -1);
        URI uri = null;
        try {
            uri = new URI(context.getRequestURI());
        } catch(URISyntaxException ex) {
            Assert.fail(ex);
        }
        String path = StringUtil.connect("", uri.getPath(), '/');
        FileInf file = WebdavFileSystem.getInstance().getFile(context, path);
        context.setContentType("text/xml; utf-8");
        Document tree = context.getResponseBuffer().getResponseXML("D:multistatus", true);
        Documents.setAttributeValue(tree, "xmlns:D", "DAV:");
        composeResponseXML(context.getRequstXML(), file, tree, depth, path);
    }

    private void composeResponseXML(Document request, FileInf file, Document response, int depth, String href) {
        String namespace = request != null ? getNamespace(request, "DAV:") : null;
        Document prop = request != null ? request.getChild(namespace + "prop") : null;
        setObjectProperties(response.addChild("D:response"), prop, file, namespace, href);
        if(depth != 0 && file.isCollection()) {
            for(FileInf obj : file.getCollection())
                setObjectProperties(response.addChild("D:response"), prop, obj, namespace, obj.getHref(href));
        }
    }

    private void setObjectProperties(Document node, Document prop, FileInf obj, String namespace, String href) {
        FileDOM dom = new FileDOM(obj);
        Document propStat = node.addChild("D:propstat");
        Document propContainer = propStat.addChild("D:prop");
        Documents.setChildValue(propStat, "D:status", StatusUtil.getStatusResult(StatusUtil.SC_OK));
        Documents.setChildValue(node, "D:href", href);
        if(prop == null)
            dom.prepareAllProperty();
        else {
            Document notFoundPropStat = null;
            for(Attribute value : prop.getChildren()) {
                String name = value.getName().replace(namespace, "");
                if(!dom.prepareProperty(name)) {
                    if(notFoundPropStat == null) {
                        notFoundPropStat = node.addChild("D:propstat");
                        Documents.setChildValue(notFoundPropStat, "D:status", StatusUtil.getStatusResult(StatusUtil.SC_NOT_FOUND));
                        notFoundPropStat = notFoundPropStat.addChild("D:prop");
                    }
                    Documents.touchChild(notFoundPropStat, "D:" + name);
                }
            }
        }
        Documents.merge(propContainer, dom);
    }

    private String getNamespace(Document tree, String name) {
        for(Attribute value : tree.getAttributes()) {
            String attrName = value.getName();
            if(attrName.startsWith("xmlns:") && value.getObject().equals(name))
                return attrName.length() > 6 ? attrName.substring(6) + ":" : null;
        }
        return name;
    }
}
