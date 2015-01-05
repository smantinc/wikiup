package org.wikiup.core.bean;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bootstrap.BootstrapResource;
import org.wikiup.core.impl.rl.ClassPathResourceLoader;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.inf.ext.ResourceLoader;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WikiupResourceLoader extends WikiupDynamicSingleton<WikiupResourceLoader> implements ResourceLoader, Releasable, Iterable<ResourceLoader> {
    private List<Node> loaders;
    private ResourceLoader defaultLoader;

    public Iterable<Resource> get(String name) {
        if(loaders.isEmpty()) {
            Iterable<Resource> iterable = defaultLoader.get(name);
            return iterable != null ? iterable : null;
        } else {
            BootstrapResource br = new BootstrapResource();
            for(Node loader : loaders) {
                Iterable<Resource> iterable = loader.get(name);
                if(iterable != null) {
                    for(Resource res : iterable)
                        br.append(res, loader.override);
                }
                br.doPackage();
            }
            return br;
        }
    }

    public void release() {
        Interfaces.releaseAll(loaders);
        loaders.clear();
        loaders = null;
    }

    public void firstBuilt() {
        defaultLoader = new ClassPathResourceLoader();
        loaders = new ArrayList<Node>();
    }

    @Override
    public void aware(Document desc) {
        for(Document doc : desc.getChildren())
            loaders.add(new Node(doc));

    }

    public Iterator<ResourceLoader> iterator() {
        List<ResourceLoader> list = new ArrayList<ResourceLoader>();
        for(ResourceLoader loader : loaders)
            list.add(loader);
        return list.iterator();
    }

    private static class Node implements ResourceLoader {
        private ResourceLoader resourceLoader;
        public boolean override;

        public Node(Document doc) {
            override = Documents.getAttributeBooleanValue(doc, "override", false);
            resourceLoader = Wikiup.build(ResourceLoader.class, doc, doc);
        }

        public Iterable<Resource> get(String name) {
            return resourceLoader.get(name);
        }
    }
}
