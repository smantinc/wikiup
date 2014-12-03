package org.wikiup.modules.webdav.imp.fs;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.FileUtil;
import org.wikiup.modules.webdav.inf.FileInf;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class NativeFileCollection implements FileInf {
    private File file;

    public NativeFileCollection(File file) {
        this.file = file;
    }

    public Iterable<FileInf> getCollection() {
        List<FileInf> list = new Vector<FileInf>();
        File files[] = file.listFiles();
        int i;
        for(i = 0; i < files.length; i++)
            if(!FileUtil.isHidden(files[i]))
                list.add(files[i].isDirectory() ? new NativeFileCollection(files[i]) : new NativeFile(files[i]));
        return list;
    }

    public String getContentLanguage() {
        return null;
    }

    public long getContentLength() {
        return 0L;
    }

    public String getContentType() {
        return null;
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
        return null;
    }

    public Date getLastModifiedDate() {
        return new Date(file.lastModified());
    }

    public OutputStream getOutputStream() {
        return null;
    }

    public boolean isCollection() {
        return true;
    }

    public void lock() {
    }

    public void unlock() {
    }

    public boolean exists() {
        return file.exists();
    }

    public String getHref(String parent) {
        return FileUtil.joinPath(parent, getDisplayName(), '/');
    }
}
