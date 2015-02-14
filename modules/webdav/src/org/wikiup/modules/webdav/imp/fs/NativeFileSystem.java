package org.wikiup.modules.webdav.imp.fs;

import org.wikiup.core.impl.document.DocumentImpl;
import org.wikiup.core.impl.document.DocumentWithNamespace;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.modules.authorization.inf.Principal;
import org.wikiup.modules.webdav.inf.FileInf;
import org.wikiup.modules.webdav.inf.FileSystemInf;
import org.wikiup.modules.webdav.util.WebdavUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.beans.ServletContextContainer;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class NativeFileSystem implements FileSystemInf, DocumentAware, ServletProcessorContextAware {
    private File repository;
    private String contextPath;
    private Document configure;

    private ServletProcessorContext context;
    private boolean anonymous;

    public FileInf getFile(String path) {
        File file = getNativeFile(path);
        return file.isDirectory() ? new NativeFileCollection(file) : new NativeFile(file);
    }

    public void aware(Document desc) {
        contextPath = context.getContextAttribute(desc, "context-path", "/webdav");
        repository = new File(context.getContextAttribute(desc, "repository", ServletContextContainer.getInstance().getRealPath("/")));
        repository.mkdirs();
        anonymous = ValueUtil.toBoolean(context.getContextAttribute(desc, "anonymous", null), true);
        configure = desc;
    }

    public FileInf createFile(String path) {
        File file = getNativeFile(path);
        try {
            file.createNewFile();
        } catch(IOException ex) {
            Assert.fail(ex);
        }
        return new NativeFile(file);
    }

    public FileInf createCollection(String path) {
        File file = getNativeFile(path);
        return file.mkdirs() ? new NativeFileCollection(file) : null;
    }

    public boolean move(String src, String dest) {
        File file = getNativeFile(src);
        return isLocked(src) ? false : file.renameTo(getNativeFile(dest));
    }

    public boolean copy(String src, String dest) {
        File sf = getNativeFile(src);
        File df = getNativeFile(dest);
        if(sf.compareTo(df) != 0) {
            try {
                FileUtil.copy(sf, df);
            } catch(IOException ex) {
                Assert.fail(ex);
            }
            if(sf.isDirectory() && df.isDirectory())
                FileUtil.delete(new File(df, ".acacia"));
        }
        return true;
    }

    private File getNativeFile(String path) {
        return FileUtil.getFile(repository.getPath(), StringUtil.shrinkLeft(StringUtil.shrinkLeft(path, "/"), StringUtil.shrinkLeft(contextPath, "/")));
    }

    public boolean hasPrivilege(String path, String dest, Principal principal, String method) {
        Document srcAcl = getFileACL(path);
        Document destAcl = getFileACL(dest);
        return srcAcl != null ? checkACL(srcAcl, destAcl, principal, method) : anonymous;
    }

    private String getMethodAccessName(String method, String name) {
        Document child = Documents.findMatchesChild(configure, name, "method", method);
        return child != null ? Documents.getAttributeValue(child, "access", null) : null;
    }

    public boolean checkACL(Document srcacl, Document destacl, Principal principal, String method) {
        return checkACL(srcacl, "permission", principal, method) && (destacl != null ? checkACL(destacl, "dest-permission", principal, method) : true);
    }

    private boolean checkACL(Document acl, String childName, Principal principal, String method) {
        if(principal != null) {
            if(principal.isSupervisor())
                return true;
            if(principal.validate())
                for(Document node : acl.getChildren()) {
                    String forName = Documents.getAttributeValue(node, "for", null);
                    if(((anonymous || !principal.isAnonymous()) && forName == null) || matches(principal.getName(), forName))
                        if(Documents.getAttributeBooleanValue(node, getMethodAccessName(method, childName), false))
                            return true;
                }
        }
        return false;
    }

    private boolean matches(String n1, String n2) {
        if(n1 != null && n2 != null)
            return n1.matches(n2.replaceAll("\\*", ".*").replaceAll("\\.\\.\\*", ".*"));
        return n1 == n2;
    }

    public Document getFileACL(String path) {
        return path != null ? getFileACL(getNativeFile(path)) : null;
    }

    public Document getFileACL(File file) {
        File acl = getFileACLFile(file);
        return acl.exists() ? Documents.loadFromFile(acl) : (file.equals(repository) ? null : getFileACL(file.getParentFile()));
    }

    private File getFileACLFile(File file) {
        return new File(getAcaciaDirectory(file), "acl.xml");
    }

    private File getFileDescription(File file, String name) {
        String fileName = FileUtil.getFileName(file.getName()) + "." + name + ".xml";
        return new File(getAcaciaDirectory(file), fileName);
    }

    private File getAcaciaDirectory(File file) {
        File acacia = new File(file.isDirectory() ? file : file.getParentFile(), ".acacia");
        if(!acacia.exists())
            acacia.mkdirs();
        return acacia;
    }

    public void setFileACL(String path, Document acl) {
        Writer writer = null;
        try {
            try {
                File file = getFileACLFile(getNativeFile(path));
                writer = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
                Documents.writeXMLHeader(writer, "utf-8");
                Documents.printToWriter(acl, writer);
            } finally {
                if(writer != null)
                    writer.close();
            }
        } catch(FileNotFoundException ex) {
            Assert.fail(ex);
        } catch(IOException ex) {
            Assert.fail(ex);
        }
    }

    public boolean lock(String src, String token, Document desc, Document response) {
        File xmlFile = getFileDescription(getNativeFile(src), "lock");
        if(!xmlFile.exists()) {
            Writer writer = null;
            try {
                writer = new FileWriter(xmlFile);
                Document request = new DocumentWithNamespace(desc, null, "DAV:");
                Document node;
                Document xml = new DocumentImpl("main");
                token = WebdavUtil.generateLockToken();
                node = xml.addChild("prop").addChild("lockdiscovery").addChild("activelock");
                Documents.merge(node.addChild("locktype"), request.getChild("locktype"));
                Documents.merge(node.addChild("lockscope"), request.getChild("lockscope"));
                Documents.merge(node.addChild("owner"), request.getChild("owner"));
                node.addChild("timeout").setObject("Second-604800");
                node.addChild("depth").setObject("Infinity");
                node.addChild("locktoken").addChild("href").setObject(token);
                node = xml.addChild("local");
                Documents.setChildValue(node, "token", token);
                Documents.setChildValue(node, "timeout", String.valueOf(System.currentTimeMillis() + 604800));
                Documents.writeXMLHeader(writer, "utf-8");
                Documents.printToWriter(xml, writer);
                writer.close();
                Documents.merge(response, xml.getChild("prop"));
                return true;
            } catch(IOException ex) {
                Assert.fail(ex);
            } finally {
                StreamUtil.close(writer);
            }
        }
        Document xml = Documents.loadFromFile(xmlFile);
        return StringUtil.compare(token, Documents.getDocumentValue(xml, "local/token", null));
    }

    private boolean isLocked(String src) {
        return getFileDescription(getNativeFile(src), "lock").exists();
    }

    public boolean unlock(String src, String token) {
        File xmlFile = getFileDescription(getNativeFile(src), "lock");
        if(xmlFile.exists()) {
            Document xml = Documents.loadFromFile(xmlFile);
            if(!StringUtil.compare(token, Documents.getDocumentValueByXPath(xml, "local/token", null)))
                return false;
            xmlFile.delete();
        }
        return true;
    }

    public boolean delete(String src) {
        File file = getNativeFile(src);
        return (file.exists() && !isLocked(src)) ? FileUtil.delete(file) : false;
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
