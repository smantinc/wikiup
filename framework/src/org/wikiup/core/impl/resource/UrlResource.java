package org.wikiup.core.impl.resource;

import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UrlResource implements Resource {
    private URL url;

    public UrlResource(URL url) {
        this.url = url;
    }

    public InputStream open() {
        try {
            return url.openStream();
        } catch(IOException ex) {
            Assert.fail(ex);
        }
        return null;
    }

    public String getURI() {
        return url.getPath();
    }

    public boolean exists() {
        return true;
    }

    public String getHost() {
        return url.getHost();
    }
}
