package org.wikiup.modules.webdav.imp.fs;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.FileUtil;
import org.wikiup.modules.webdav.imp.WebdavFileSystem;
import org.wikiup.modules.webdav.inf.FileInf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;


public class NativeFile implements FileInf, Comparable<NativeFile> {
    private File file;

    public NativeFile(File file) {
        this.file = file;
    }

    public Iterable<FileInf> getCollection() {
        return null;
    }

    public String getContentLanguage() {
        return "cn";
    }

    public long getContentLength() {
        return file.length();
    }

    public String getContentType() {
        return WebdavFileSystem.getInstance().getContentTypeByExtension(FileUtil.getFileExt(file.getName()));
    }

    public Date getCreationDate() {
        return new Date(file.lastModified());
    }

    public String getDisplayName() {
        return file.getName();
    }

    public Document getExtraAttribute(String name) {
        return null;
    }

    public Iterator getExtraAttributes() {
        return Null.getInstance();
    }

    public InputStream getInputStream() {
        try {
            return new FileInputStream(file);
        } catch(FileNotFoundException ex) {
            Assert.fail(ex);
        }
        return null;
    }

    public Date getLastModifiedDate() {
        return new Date(file.lastModified());
    }

    public OutputStream getOutputStream() {
        try {
            return new FileOutputStream(file);
        } catch(FileNotFoundException ex) {
            Assert.fail(ex);
        }
        return null;
    }

    public boolean isCollection() {
        return false;
    }

    public boolean exists() {
        return file.exists();
    }

    public String getHref(String parent) {
        return FileUtil.joinPath(parent, getDisplayName(), '/');
    }

    public int compareTo(NativeFile other) {
        return file.compareTo(other.file);
    }
}
