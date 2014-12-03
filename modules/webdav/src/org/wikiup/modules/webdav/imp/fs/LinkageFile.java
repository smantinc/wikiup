package org.wikiup.modules.webdav.imp.fs;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.FileUtil;
import org.wikiup.modules.webdav.inf.FileInf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;

public class LinkageFile implements FileInf {
    private FileInf file;
    private String displayName;
    private String href;

    public LinkageFile(String name, String href, FileInf file) {
        displayName = name;
        this.href = href;
        this.file = file;
    }

    public boolean exists() {
        return file.exists();
    }

    public Iterable<FileInf> getCollection() {
        return file.getCollection();
    }

    public String getContentLanguage() {
        return file.getContentLanguage();
    }

    public long getContentLength() {
        return file.getContentLength();
    }

    public String getContentType() {
        return file.getContentType();
    }

    public Date getCreationDate() {
        return file.getCreationDate();
    }

    public String getDisplayName() {
        return displayName;
    }

    public Document getExtraAttribute(String name) {
        return file.getExtraAttribute(name);
    }

    public Iterator getExtraAttributes() {
        return file.getExtraAttributes();
    }

    public InputStream getInputStream() {
        return file.getInputStream();
    }

    public Date getLastModifiedDate() {
        return file.getLastModifiedDate();
    }

    public OutputStream getOutputStream() {
        return file.getOutputStream();
    }

    public boolean isCollection() {
        return file.isCollection();
    }

    public String getHref(String parent) {
        return href != null ? href : FileUtil.joinPath(parent, getDisplayName(), '/');
    }
}
