package org.wikiup.core.bootstrap.impl.handler;

import org.wikiup.core.bootstrap.inf.ResourceHandler;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Resource;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StringUtil;

public abstract class DirectoryDocumentResourceHandler implements ResourceHandler, DocumentAware {
    private String base;

    public void setBase(String pattern) {
        base = StringUtil.shrinkLeft(pattern, "/");
        if(base.endsWith("/*"))
            base = base.substring(0, base.length() - 1);
    }

    public void aware(Document desc) {
        setBase(Documents.getDocumentValue(desc, "base"));
    }

    public String getRelativeDirectoryPath(String uri) {
        return FileUtil.getFilePath(StringUtil.shrinkLeft(StringUtil.shrinkLeft(uri, "/"), base), '/');
    }

    public void handle(Resource resource) {
        Document doc = Documents.loadFromResource(resource);
        String[] path = StringUtil.splitNamespaces(getRelativeDirectoryPath(resource.getURI()));
        loadDirectoryResource(resource, doc, path);
    }

    abstract protected void loadDirectoryResource(Resource resource, Document doc, String[] path);

    public void finish() {
    }
}
