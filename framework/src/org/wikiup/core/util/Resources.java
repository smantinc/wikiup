package org.wikiup.core.util;

import org.wikiup.core.impl.resource.FileResource;
import org.wikiup.core.impl.resource.JarFileResource;
import org.wikiup.core.inf.ext.Resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Resources {
    static public void setResourceBase(Iterable<Resource> resources, String base) {
        for(Resource resource : resources)
            if(resource instanceof FileResource)
                ((FileResource) resource).setBase(base);
    }

    static public Collection<Resource> getClassPathResources(String name) {
        return getClassPathResources(name, Thread.currentThread().getContextClassLoader());
    }

    static public Collection<Resource> getClassPathResources(String name, ClassLoader cl) {
        Set<Resource> res = new HashSet<Resource>();
        try {
            Enumeration<URL> e = cl.getResources(name);
            while(e.hasMoreElements()) {
                URL url = e.nextElement();
                addResourceLoader(res, url.getProtocol(), name, url.getPath());
            }
        } catch(IOException ex) {
            Assert.fail(ex);
        }
        return res;
    }

    static public Collection<Resource> getFileResources(String name) {
        Set<Resource> res = new HashSet<Resource>();
        File file = new File(name);
        if(file.exists())
            if(file.isDirectory())
                res.addAll(getResourcesFromDirectory(file));
            else
                res.add(new FileResource(file));
        return res;
    }

    static private void addResourceLoader(Set<Resource> res, String protocol, String name, String path) throws IOException {
        if(protocol.equals("file"))
            res.addAll(getFileResources(path));
        else if(protocol.equals("jar"))
            res.addAll(getResourcesFromJAR(name, path));
    }

    static public Collection<Resource> getResourcesFromDirectory(File file) {
        Set<Resource> res = new HashSet<Resource>();
        File[] files = file.listFiles();
        if(files != null)
            for(File entry : files)
                if(!FileUtil.isHidden(file)) {
                    if(entry.isDirectory())
                        res.addAll(getResourcesFromDirectory(entry));
                    else
                        res.add(new FileResource(entry));
                }
        return res;
    }

    static public Collection<Resource> getResourcesFromJAR(String name, String path) throws IOException {
        Set<Resource> res = new HashSet<Resource>();
        if(StringUtil.shrinkRight(path, "/").endsWith("!/" + name)) {
            int idx = path.indexOf(':');
            String fileName = FileUtil.decodeFileName(path.substring(idx != -1 ? idx + 1 : 0, path.lastIndexOf("!/" + name)));
            JarFile jarFile = new JarFile(fileName);
            Enumeration<JarEntry> e = jarFile.entries();
            while(e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                if(!jarEntry.isDirectory())
                    if(jarEntry.getName().startsWith(name))
                        res.add(new JarFileResource(jarFile, jarEntry));
            }
        }
        return res;
    }
}
