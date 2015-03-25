package org.wikiup.framework.bootstrap.impl.handler;

import org.wikiup.core.Wikiup;
import org.wikiup.framework.bootstrap.inf.ExtendableDocumentResource;
import org.wikiup.framework.bootstrap.inf.ResourceHandler;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ExtendableDocumentResourceHandler implements ResourceHandler, DocumentAware {
    private Collection<DocumentResourceLoader> loaders = new LinkedList<DocumentResourceLoader>();

    public void handle(Resource resource) {
        Document doc = Documents.loadXmlFromStream(resource.open(), true);
        for(DocumentResourceLoader loader : loaders)
            loader.load(doc);
    }

    public void aware(Document desc) {
        Wikiup wk = Wikiup.getInstance();
        for(Document node : desc.getChildren("extendable-document")) {
            ExtendableDocumentResource dr = wk.get(ExtendableDocumentResource.class, node);
            Assert.notNull(dr, node.toString());
            loaders.add(new DocumentResourceLoader(dr));
        }
    }

    public void finish() {
        for(DocumentResourceLoader loader : loaders)
            loader.finish();
    }

    static private class DocumentResourceLoader {
        private ExtendableDocumentResource<Object> documentResource;
        private List<Object> resources = new ArrayList<Object>();
        private Map<String, Object> nameMap = new HashMap<String, Object>();

        public DocumentResourceLoader(ExtendableDocumentResource<Object> documentResource) {
            this.documentResource = documentResource;
        }

        public void load(Document doc) {
            for(Object resource : documentResource.loadResources(doc)) {
                String name = documentResource.getResourceName(resource);
                if(name != null)
                    nameMap.put(name, resource);
                resources.add(resource);
            }
        }

        public void finish() {
            updateHierary();
            documentResource.finish(nameMap, resources);
        }

        private void updateHierary() {
            for(Object resource : resources) {
                String name = documentResource.getSuperResourceName(resource);
                if(name != null) {
                    Object sup = nameMap.get(name);
                    documentResource.extend(resource, sup);
                    if(documentResource.getResourceName(resource) == null)
                        nameMap.put(name, resource);
                }
            }
        }
    }
}
