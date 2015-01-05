package org.wikiup.servlet.impl.rl;

import org.wikiup.core.bean.WikiupNamingDirectory;
import org.wikiup.core.bootstrap.inf.ResourceHandler;
import org.wikiup.core.impl.resource.JarFileResource;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.inf.ext.LogicalTranslator;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WebArchiveResourceHandler implements ResourceHandler, DocumentAware {
    private String repository;
    private String base;

    public void setBase(String pattern) {
        base = StringUtil.shrinkLeft(pattern, "/");
        if(base.endsWith("/*"))
            base = base.substring(0, base.length() - 1);
    }

    public String getRelativeDirectoryPath(String uri) {
        return FileUtil.getFilePath(StringUtil.shrinkLeft(StringUtil.shrinkLeft(uri, "/"), base), '/');
    }

    public void handle(Resource resource) {
        String relativePath = getRelativeDirectoryPath(resource.getURI());
        File file = FileUtil.getFile(FileUtil.joinPath(repository, relativePath));
        String ext = FileUtil.getFileExt(resource.getURI());
        file.mkdirs();
        if(StringUtil.compareIgnoreCase(ext, "war"))
            extractArchiveToDirectory(resource, file);
        else
            copyArchiveToDirectory(resource, file);
    }

    private void copyArchiveToDirectory(Resource resource, File file) {
        String fileName = FileUtil.getFileNameExt(resource.getURI());
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(file, fileName));
            StreamUtil.copy(os, resource.open());
        } catch(FileNotFoundException e) {
        } finally {
            StreamUtil.close(os);
        }
    }

    private void extractArchiveToDirectory(Resource resource, final File file) {
        File resourceFile = getResourceFile(resource);
        final long lastModified = resourceFile != null ? resourceFile.lastModified() : Long.MAX_VALUE;
        InputStream is = resource.open();
        try {
            FileUtil.unzip(is, file, new LogicalTranslator<String>() {
                public Boolean translate(String object) {
                    return !(object.startsWith("WEB-INF") || object.startsWith("META-INF") || FileUtil.isUpToDate(new File(file, object), lastModified));
                }
            });
        } catch(IOException e) {
            Assert.fail(e);
        } finally {
            StreamUtil.close(is);
        }
    }

    public void finish() {
    }

    public void aware(Document desc) {
        repository = StringUtil.evaluateEL(Documents.getDocumentValue(desc, "repository", "${sc:real-path:/}"), WikiupNamingDirectory.getInstance());
        setBase(Documents.getDocumentValue(desc, "base"));
    }

    private File getResourceFile(Resource res) {
        if(res instanceof JarFileResource)
            return new File(((JarFileResource) res).getJar().getName());
        return null;
    }
}
