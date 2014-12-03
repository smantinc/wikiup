package org.wikiup.modules.spring;

import org.wikiup.core.inf.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;


public class SpringResource implements org.springframework.core.io.Resource {
    private Resource resource;

    public SpringResource(Resource resource) {
        this.resource = resource;
    }

    public InputStream getInputStream() throws IOException {
        try {
            return resource.open();
        } catch(Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public boolean exists() {
        return resource.exists();
    }

    public boolean isReadable() {
        return true;
    }

    public boolean isOpen() {
        return false;
    }

    public URL getURL() throws IOException {
        return null;
    }

    public URI getURI() throws IOException {
        return null;
    }

    public File getFile() throws IOException {
        return null;
    }

    public long lastModified() throws IOException {
        return 0L;
    }

    public org.springframework.core.io.Resource createRelative(String string) throws IOException {
        return null;
    }

    public String getFilename() {
        return resource.getURI();
    }

    public String getDescription() {
        return getFilename();
    }
}
