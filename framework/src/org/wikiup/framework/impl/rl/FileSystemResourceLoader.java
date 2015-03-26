package org.wikiup.framework.impl.rl;

import org.wikiup.Wikiup;
import org.wikiup.framework.bean.WikiupNamingDirectory;
import org.wikiup.core.impl.iterable.IterableCollection;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.inf.ext.ResourceLoader;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Resources;
import org.wikiup.core.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileSystemResourceLoader implements ResourceLoader, DocumentAware {
    private List<File> roots = new ArrayList<File>();

    public Iterable<Resource> get(String name) {
        IterableCollection<Resource> ic = new IterableCollection<Resource>();
        for(File root : roots) {
            File f = new File(root, name);
            Collection<Resource> resources = Resources.getFileResources(f.getAbsolutePath());
            Resources.setResourceBase(resources, root.getAbsolutePath());
            ic.append(resources);
        }
        return ic;
    }

    public void aware(Document desc) {
        WikiupNamingDirectory wnd = Wikiup.getModel(WikiupNamingDirectory.class);
        for(Document doc : desc.getChildren()) {
            String path = StringUtil.evaluateEL(Documents.getDocumentValue(doc), wnd);
            File file = new File(path);
            if(file.isDirectory())
                roots.add(file);
        }
    }
}
