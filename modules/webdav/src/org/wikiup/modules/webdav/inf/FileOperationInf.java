package org.wikiup.modules.webdav.inf;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileOperationInf {
    public InputStream getInputStream();

    public OutputStream getOutputStream();

    public boolean exists();
}
