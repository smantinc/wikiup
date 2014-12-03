package org.wikiup.modules.webdav.imp.fs;

import org.wikiup.core.impl.iterable.IterableCollection;
import org.wikiup.core.inf.Document;
import org.wikiup.modules.webdav.inf.FileInf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class MergeFileCollection implements FileInf {
    private FileInf File;
    private List<FileInf> files = new Vector<FileInf>();

    public MergeFileCollection(FileInf file) {
        File = file;
    }

    public void addFile(FileInf file) {
        files.add(file);
    }

    public boolean exists() {
        return File.exists();
    }

    public Iterable<FileInf> getCollection() {
        return new IterableCollection<FileInf>(File.getCollection(), files);
    }

    public String getContentLanguage() {
        return File.getContentLanguage();
    }

    public long getContentLength() {
        return File.getContentLength();
    }

    public String getContentType() {
        return File.getContentType();
    }

    public Date getCreationDate() {
        return File.getCreationDate();
    }

    public String getDisplayName() {
        return File.getDisplayName();
    }

    public Document getExtraAttribute(String name) {
        return File.getExtraAttribute(name);
    }

    public Iterator getExtraAttributes() {
        return File.getExtraAttributes();
    }

    public String getHref(String parent) {
        return File.getHref(parent);
    }

    public InputStream getInputStream() {
        return File.getInputStream();
    }

    public Date getLastModifiedDate() {
        return File.getLastModifiedDate();
    }

    public OutputStream getOutputStream() {
        return File.getOutputStream();
    }

    public boolean isCollection() {
        return File.isCollection();
    }
}
