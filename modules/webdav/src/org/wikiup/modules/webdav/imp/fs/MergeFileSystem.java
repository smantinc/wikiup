package org.wikiup.modules.webdav.imp.fs;

import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.document.MergedDocument;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.modules.authorization.inf.Principal;
import org.wikiup.modules.webdav.inf.FileInf;
import org.wikiup.modules.webdav.inf.FileSystemInf;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class MergeFileSystem implements FileSystemInf, DocumentAware, ServletProcessorContextAware {
    private ServletProcessorContext context;
    private Document configure;

    public boolean copy(String src, String dest) {
        return getFileSystem(src).copy(src, dest);
    }

    public FileInf createCollection(String path) {
        return getFileSystem(path).createCollection(path);
    }

    public FileInf createFile(String path) {
        return getFileSystem(path).createFile(path);
    }

    public boolean delete(String src) {
        return getFileSystem(src).delete(src);
    }

    public FileInf getFile(String path) {
        return getFileSystem(path).getFile(path);
    }

    public Document getFileACL(String path) {
        return getFileSystem(path).getFileACL(path);
    }

    public boolean hasPrivilege(String src, String dest, Principal principal, String method) {
        return getFileSystem(src).hasPrivilege(src, dest, principal, method);
    }

    public boolean lock(String src, String token, Document desc, Document response) {
        return getFileSystem(src).lock(src, token, desc, response);
    }

    public boolean move(String src, String dest) {
        return getFileSystem(src).move(src, dest);
    }

    public void setFileACL(String path, Document acl) {
        getFileSystem(path).setFileACL(path, acl);
    }

    public boolean unlock(String src, String token) {
        return getFileSystem(src).unlock(src, token);
    }

    public void aware(Document desc) {
        configure = desc;
    }

    private boolean compareFileSystem(String source, String dest) {
        return getFileSystemNode(source) == getFileSystemNode(dest);
    }

    private Document getFileSystemNode(String path) {
        for(Document node : configure.getChildren("file-system"))
            if(path.matches(Documents.getAttributeValue(node, "pattern")))
                return node;
        return null;
    }

    private Document getFileSystemConfigure(String path) {
        Document node = getFileSystemNode(path);
        Document shared = configure.getChild("file-system-shared");
        return shared != null ? new MergedDocument(shared, node) : node;
    }

    private FileSystemInf getFileSystem(String path) {
        return getFileSystem(getFileSystemConfigure(path));
    }

    private FileSystemInf getFileSystem(Document node) {
        BeanContainer mc = node != null ? Wikiup.getInstance().getModelProvider(FileSystemInf.class, node) : null;
        context.awaredBy(mc);
        Interfaces.initialize(mc, node);
        return mc != null ? mc.query(FileSystemInf.class) : null;
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
