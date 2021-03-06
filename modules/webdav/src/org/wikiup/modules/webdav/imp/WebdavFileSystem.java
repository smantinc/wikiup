package org.wikiup.modules.webdav.imp;

import java.util.HashMap;
import java.util.Map;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.impl.document.DocumentImpl;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.modules.authorization.AuthorizationManager;
import org.wikiup.modules.authorization.inf.AuthorizationContextInf;
import org.wikiup.modules.webdav.inf.FileInf;
import org.wikiup.modules.webdav.inf.FileSystemInf;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.beans.MimeTypes;

public class WebdavFileSystem extends WikiupDynamicSingleton<WebdavFileSystem> implements DocumentAware, Dictionary<FileSystemInf> {
    private DocumentImpl configure;
    private Map<String, FileSystemInf> fileSystems;

    private Document defaultFileSystemConfigure;

    static public WebdavFileSystem getInstance() {
        return getInstance(WebdavFileSystem.class);
    }

    public FileInf getFile(ServletProcessorContext context, String path) {
        FileSystemInf fs = getFileSystem(context, path);
        return fs != null ? fs.getFile(path) : null;
    }

    public boolean move(ServletProcessorContext context, String src, String dest) {
        FileSystemInf fs = getFileSystem(context, src);
        return fs != null && fs.move(src, dest);
    }

    public boolean copy(ServletProcessorContext context, String src, String dest) {
        FileSystemInf fs = getFileSystem(context, src);
        return fs != null && fs.copy(src, dest);
    }

    public boolean lock(ServletProcessorContext context, String src, String token, Document desc, Document response) {
        FileSystemInf fs = getFileSystem(context, src);
        return fs != null && fs.lock(src, token, desc, response);
    }

    public boolean unlock(ServletProcessorContext context, String src, String token) {
        FileSystemInf fs = getFileSystem(context, src);
        return fs != null && fs.unlock(src, token);
    }

    public FileInf createFileCollection(ServletProcessorContext context, String path) {
        FileSystemInf fs = getFileSystem(context, path);
        return fs != null ? fs.createCollection(path) : null;
    }

    public FileSystemInf getFileSystem(ServletProcessorContext context, String path) {
        return getFileSystem(context, getFileSystemConfigure(path));
    }

    public FileSystemInf getFileSystem(ServletProcessorContext context, Document node) {
        FileSystemInf provider = node != null ? Wikiup.getInstance().getBean(FileSystemInf.class, node) : null;
        if(provider != null) {
            context.awaredBy(provider);
            Interfaces.initialize(provider, node);
        }
        return provider;
    }

    public Document getFileSystemConfigure(String path) {
        Document conf = Documents.findMatchesChild(configure, "uri-pattern", path);
        return conf != null ? conf : defaultFileSystemConfigure;
    }

    public void aware(Document desc) {
        for(Document node : desc.getChildren("file-system")) {
            String uriPattern = Documents.getAttributeValue(node, "uri-pattern", null);
            configure.addChild(node);
            if(uriPattern == null)
                defaultFileSystemConfigure = node;
        }
    }

    public String getContentTypeByExtension(String ext) {
        String contentType = Wikiup.getModel(MimeTypes.class).get(ext);
        return contentType != null ? contentType : "application/archive";
    }

    public AuthorizationContextInf getAuthorizationContext(ServletProcessorContext context, FileSystemInf fs, String path) {
        return Wikiup.getModel(AuthorizationManager.class).getAuthorizationContext(context);
    }

    public void firstBuilt() {
        configure = new DocumentImpl("file-systems");
        fileSystems = new HashMap<String, FileSystemInf>();
    }

    public FileSystemInf get(String name) {
        return fileSystems.get(name);
    }
}
