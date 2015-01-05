package org.wikiup.core.inf.ext;

import java.io.InputStream;

public interface Resource {
    public String getHost();
    public String getURI();
    public boolean exists();
    public InputStream open();
}
