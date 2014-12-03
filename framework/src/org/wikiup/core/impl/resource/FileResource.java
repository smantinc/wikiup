package org.wikiup.core.impl.resource;

import org.wikiup.core.inf.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileResource implements Resource {
    private File file;
    private String base;
    private String uri;

    public FileResource(String path) {
        this.file = new File(path);
    }

    public FileResource(File file) {
        this.file = file;
    }

    public void setBase(String base) {
        this.base = FileUtil.getSystemFilePath(base);
        this.uri = getPathURI(StringUtil.shrinkLeft(file.getPath(), this.base));
    }

    public InputStream open() {
        try {
            return new FileInputStream(file);
        } catch(FileNotFoundException ex) {
            Assert.fail(ex);
            return null;
        }
    }

    public String getURI() {
        return uri != null ? uri : file.getPath();
    }

    public boolean exists() {
        return file.exists();
    }

    public String getHost() {
        return base;
    }

    public File getFile() {
        return file;
    }

    private String getPathURI(String path) {
        return StringUtil.connect("/", StringUtil.replaceAll(path, File.separatorChar, '/'), '/');
    }
}
