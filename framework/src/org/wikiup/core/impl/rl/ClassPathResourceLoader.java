package org.wikiup.core.impl.rl;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupNamingDirectory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Resource;
import org.wikiup.core.inf.ext.ResourceLoader;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Resources;
import org.wikiup.core.util.StringUtil;

public class ClassPathResourceLoader implements ResourceLoader, DocumentAware {
    private String base;

    public Iterable<Resource> get(String name) {
        Iterable<Resource> resources = Resources.getClassPathResources(name);
        Resources.setResourceBase(resources, base);
        return resources;
    }

    public void aware(Document desc) {
        base = StringUtil.evaluateEL(Documents.getDocumentValue(desc, "base"), Wikiup.getModel(WikiupNamingDirectory.class));
    }
}