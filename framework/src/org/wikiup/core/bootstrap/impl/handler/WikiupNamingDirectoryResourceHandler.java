package org.wikiup.core.bootstrap.impl.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupNamingDirectory;
import org.wikiup.core.bean.WikiupPluginManager;
import org.wikiup.core.impl.document.MergedDocument;
import org.wikiup.core.impl.resource.JarFileResource;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;

public class WikiupNamingDirectoryResourceHandler extends DirectoryDocumentResourceHandler {
    private static final String WK_DESC_NAME = "desc";
    private static final String WK_PATH_NAME = "path";
    private static final String WK_RESOURCE_NAME = "resource";

    private Document container = Documents.create("root");
    private WikiupNamingDirectory wnd = WikiupNamingDirectory.getInstance();

    private HashMap<String, DirectoryList> directories = new HashMap<String, DirectoryList>();

    @Override
    public void finish() {
/*
        loadDirectory(container);
/*/
        ArrayList<DirectoryList> sorted = new ArrayList<DirectoryList>(directories.values());
        Collections.sort(sorted);

        for(DirectoryList directoryList : sorted)
            directoryList.create(wnd);
/**/
    }

    @Override
    protected void loadDirectoryResource(Resource resource, Document doc, String[] path) {
        Document item = Documents.touchDocument(container, path, path.length);
        Document attr = (Document) getAttribute(item, WK_DESC_NAME);
        boolean virtual = attr != null && Documents.getAttributeBooleanValue(attr, "virtual", false);
        Documents.setAttributeValue(item, WK_DESC_NAME, attr != null ? (virtual ? new MergedDocument(doc, attr) : new MergedDocument(attr, doc)) : doc);
        Documents.setAttributeValue(item, WK_PATH_NAME, path);
        Documents.setAttributeValue(item, WK_RESOURCE_NAME, resource);

        String key = StringUtil.join(path, "/");
        DirectoryList directoryList = directories.get(key);
        if(directoryList == null)
            directories.put(key, directoryList = new DirectoryList(path));
        directoryList.directories.add(new Directory(doc, resource));
    }

    private void loadDirectory(Document doc) {
        String[] path = (String[]) getAttribute(doc, WK_PATH_NAME);
        Document desc = (Document) getAttribute(doc, WK_DESC_NAME);
        Resource resource = (Resource) getAttribute(doc, WK_RESOURCE_NAME);
        if(desc != null && path != null) {
            try {
                Object directory = wnd.get(path);
                if(Wikiup.getCsidAttribute(desc, null) != null) {
                    Object bean = Wikiup.getInstance().getBean(Object.class, desc);
                    Interfaces.initialize(bean, desc);
                    wnd.set(path, bean);
                } else if(directory != null)
                    Interfaces.initialize(directory, desc);
                else
                    createContainer(desc, path);
            } catch(Exception ex) {
                JarFileResource jarFile = Interfaces.cast(JarFileResource.class, resource);
                if(jarFile != null) {
                    WikiupPluginManager.Plugin plugin = Wikiup.getModel(WikiupPluginManager.class).getPluginByJar(jarFile.getJar());
                    if(plugin != null)
                        plugin.setStatus(WikiupPluginManager.Plugin.Status.incompatible);
                    else
                        Assert.fail(ex);
                } else
                    Assert.fail(ex);
            }
        }
        for(Document node : doc.getChildren())
            loadDirectory(node);
    }

    private Object getAttribute(Document doc, String name) {
        Attribute attr = doc.getAttribute(name);
        return attr != null ? attr.getObject() : null;
    }

    private void createContainer(Document doc, String[] path) {
        Object container = wnd.get(path);
        if(container != null)
            Interfaces.initialize(container, doc);
        else
            wnd.create(path, doc, path.length);
    }

    private static class DirectoryList implements Comparable<DirectoryList> {
        public String[] path;
        public LinkedList<Directory> directories = new LinkedList<Directory>();

        public DirectoryList(String[] path) {
            this.path = path;
        }

        public void create(WikiupNamingDirectory wnd) {
            Object directory = wnd.get(path);
            Document desc = getDocument();
            if(Wikiup.getCsidAttribute(desc, null) != null) {
                Object bean = Wikiup.getInstance().getBean(Object.class, desc);
                Interfaces.initialize(bean, desc);
                wnd.set(path, bean);
            } else if(directory != null)
                Interfaces.initialize(directory, desc);
            else
                create(wnd, desc);
        }

        private void create(WikiupNamingDirectory wnd, Document doc) {
            Object container = wnd.get(path);
            if(container != null)
                Interfaces.initialize(container, doc);
            else
                wnd.create(path, doc, path.length);
        }

        @Override
        public int compareTo(DirectoryList o) {
            return path.length - o.path.length;
        }
        
        private Document getDocument() {
            Document doc = null;
            for(Directory directory : directories) {
                if(doc == null)
                    doc = directory.document;
                else if(!Documents.getAttributeBooleanValue(doc, "virtual", false))
                    doc = new MergedDocument(directory.document, doc);
            }
            return doc;
        }
    }

    private static class Directory {
        public Document document;
        public Resource resource;

        public Directory(Document document, Resource resource) {
            this.document = document;
            this.resource = resource;
        }
    }
}
