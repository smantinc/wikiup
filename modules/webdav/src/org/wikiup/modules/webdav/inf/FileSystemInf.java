package org.wikiup.modules.webdav.inf;

import org.wikiup.core.inf.Document;
import org.wikiup.modules.authorization.inf.Principal;

public interface FileSystemInf {
    public FileInf getFile(String path);

    public FileInf createFile(String path);

    public FileInf createCollection(String path);

    public boolean move(String src, String dest);

    public boolean copy(String src, String dest);

    public boolean delete(String src);

    public boolean lock(String src, String token, Document desc, Document response);

    public boolean unlock(String src, String token);

    public boolean hasPrivilege(String src, String dest, Principal principal, String method);

    public void setFileACL(String path, Document acl);

    public Document getFileACL(String path);
}
