package org.wikiup.core.impl.resource;

import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileResource implements Resource {
    private JarFile jar;
    private JarEntry entry;

    public JarFileResource(JarFile jar, JarEntry entry) {
        this.jar = jar;
        this.entry = entry;
    }

    public InputStream open() {
        try {
            return jar.getInputStream(entry);
        } catch(IOException ex) {
            Assert.fail(ex);
        }
        return null;
    }

    public String getURI() {
        return StringUtil.connect("/", entry.getName(), '/');
    }

    public boolean exists() {
        return jar != null && entry != null;
    }

    public String getHost() {
        return jar.getName();
    }

    public JarFile getJar() {
        return jar;
    }

    public JarEntry getEntry() {
        return entry;
    }

    @Override
    public int hashCode() {
        return StringUtil.connect(getHost(), getURI(), '!').hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        JarFileResource resource = Interfaces.cast(JarFileResource.class, obj);
        return obj != null && resource.getURI().equals(getURI()) && resource.getHost().equals(getHost());
    }
}
